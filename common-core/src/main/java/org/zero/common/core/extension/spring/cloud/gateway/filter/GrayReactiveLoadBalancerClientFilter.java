package org.zero.common.core.extension.spring.cloud.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.CompletionContext;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycle;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycleValidator;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.ResponseData;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_LOADBALANCER_RESPONSE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * 支持灰度功能的 {@link ReactiveLoadBalancerClientFilter} 实现类
 * <p>
 * 因为要保证两者共存（lb 协议的负载均衡处理还需 {@link ReactiveLoadBalancerClientFilter}），
 * 此外由于 {@link ReactiveLoadBalancerClientFilter} 中大部分方法是 private 的，无法进行重写，
 * 因此不继承 {@link ReactiveLoadBalancerClientFilter}，改为 copy 然后修改。
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration#gatewayLoadBalancerClientFilter(org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory, org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties)
 * @since 2025/7/16
 */
@RequiredArgsConstructor
@CommonsLog
public class GrayReactiveLoadBalancerClientFilter implements GlobalFilter, Ordered {
    protected final LoadBalancerClientFactory clientFactory;
    protected final GatewayLoadBalancerProperties properties;
    protected final LoadBalancer loadBalancer;

    /**
     * copy from {@link ReactiveLoadBalancerClientFilter#filter(ServerWebExchange, GatewayFilterChain)} and modify
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        // Modify：将 ReactiveLoadBalancerClientFilter 源码的 lb 替换成 glb，表示灰度负载均衡
        if (url == null || (!"glb".equals(url.getScheme()) && !"glb".equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        // preserve the original url
        addOriginalRequestUrl(exchange, url);

        if (log.isTraceEnabled()) {
            log.trace(this.getClass().getSimpleName() + " url before: " + url);
        }

        URI requestUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String serviceId = requestUri.getHost();
        Set<LoadBalancerLifecycle> supportedLifecycleProcessors = LoadBalancerLifecycleValidator
                .getSupportedLifecycleProcessors(clientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
                        RequestDataContext.class, ResponseData.class, ServiceInstance.class);
        DefaultRequest<RequestDataContext> lbRequest = new DefaultRequest<>(
                new RequestDataContext(new RequestData(exchange.getRequest()), getHint(serviceId)));
        LoadBalancerProperties loadBalancerProperties = clientFactory.getProperties(serviceId);
        return this.choose(lbRequest, serviceId, supportedLifecycleProcessors).doOnNext(response -> {

                    if (!response.hasServer()) {
                        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                                .onComplete(new CompletionContext<>(CompletionContext.Status.DISCARD, lbRequest, response)));
                        throw NotFoundException.create(properties.isUse404(), "Unable to find instance for " + url.getHost());
                    }

                    ServiceInstance retrievedInstance = response.getServer();

                    URI uri = exchange.getRequest().getURI();

                    // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
                    // if the loadbalancer doesn't provide one.
                    String overrideScheme = retrievedInstance.isSecure() ? "https" : "http";
                    if (schemePrefix != null) {
                        overrideScheme = url.getScheme();
                    }

                    DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(retrievedInstance,
                            overrideScheme);

                    URI requestUrl = reconstructURI(serviceInstance, uri);

                    if (log.isTraceEnabled()) {
                        log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
                    }
                    exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
                    exchange.getAttributes().put(GATEWAY_LOADBALANCER_RESPONSE_ATTR, response);
                    supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, response));
                }).then(chain.filter(exchange))
                .doOnError(throwable -> supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                        .onComplete(new CompletionContext<ResponseData, ServiceInstance, RequestDataContext>(
                                CompletionContext.Status.FAILED, throwable, lbRequest,
                                exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR)))))
                .doOnSuccess(aVoid -> supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                        .onComplete(new CompletionContext<ResponseData, ServiceInstance, RequestDataContext>(
                                CompletionContext.Status.SUCCESS, lbRequest,
                                exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR), buildResponseData(exchange,
                                loadBalancerProperties.isUseRawStatusCodeInResponseData())))));
    }

    /**
     * copy from {@link ReactiveLoadBalancerClientFilter#choose(Request, String, Set)} and modify
     */
    protected Mono<Response<ServiceInstance>> choose(Request<RequestDataContext> lbRequest, String serviceId,
                                                     Set<LoadBalancerLifecycle> supportedLifecycleProcessors) {
        ReactorLoadBalancer<ServiceInstance> reactorLoadBalancer = new GrayReactorServiceInstanceLoadBalancer(serviceId,
                clientFactory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class),
                loadBalancer);
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));
        return reactorLoadBalancer.choose(lbRequest);
    }

    /**
     * copy from {@link ReactiveLoadBalancerClientFilter#getHint(String)}
     */
    protected String getHint(String serviceId) {
        LoadBalancerProperties loadBalancerProperties = clientFactory.getProperties(serviceId);
        Map<String, String> hints = loadBalancerProperties.getHint();
        String defaultHint = hints.getOrDefault("default", "default");
        String hintPropertyValue = hints.get(serviceId);
        return hintPropertyValue != null ? hintPropertyValue : defaultHint;
    }

    /**
     * copy from {@link ReactiveLoadBalancerClientFilter#buildResponseData(ServerWebExchange, boolean)}
     */
    protected ResponseData buildResponseData(ServerWebExchange exchange, boolean useRawStatusCodes) {
        if (useRawStatusCodes) {
            return new ResponseData(new RequestData(exchange.getRequest()), exchange.getResponse());
        }
        return new ResponseData(exchange.getResponse(), new RequestData(exchange.getRequest()));
    }

    /**
     * copy from {@link ReactiveLoadBalancerClientFilter#reconstructURI(ServiceInstance, URI)}
     */
    protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
    }

    @Override
    public int getOrder() {
        return ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER - 10;
    }
}
