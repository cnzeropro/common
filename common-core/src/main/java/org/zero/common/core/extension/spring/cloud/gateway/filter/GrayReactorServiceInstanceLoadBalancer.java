package org.zero.common.core.extension.spring.cloud.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @see GrayReactiveLoadBalancerClientFilter
 * @see org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration#reactorServiceInstanceLoadBalancer(org.springframework.core.env.Environment, org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory)
 * @see com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancerClientConfiguration#nacosLoadBalancer(org.springframework.core.env.Environment, org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory, com.alibaba.cloud.nacos.NacosDiscoveryProperties)
 * @since 2025/7/16
 */
@CommonsLog
@RequiredArgsConstructor
public class GrayReactorServiceInstanceLoadBalancer implements ReactorServiceInstanceLoadBalancer {
    protected static final String VERSION = "version";
    protected static final String TAG = "tag";

    /**
     * 服务实例名
     */
    protected final String serviceId;
    /**
     * serviceId 对应的服务实例的列表
     */
    protected final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    /**
     * 负载均衡器
     */
    protected final LoadBalancer loadBalancer;

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        // 获得 HttpHeaders 属性，实现从 header 中获取 version
        HttpHeaders headers = ((RequestDataContext) request.getContext()).getClientRequest().getHeaders();
        // 选择实例
        return serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new)
                .get(request)
                .next()
                .map(instances -> this.getInstanceResponse(instances, headers));
    }

    protected Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, HttpHeaders headers) {
        // 如果服务实例列表为空，则直接返回
        if (CollectionUtils.isEmpty(instances)) {
            if (log.isWarnEnabled()) {
                log.warn(String.format("Service[%s] instance is empty", serviceId));
            }
            return new EmptyResponse();
        }

        List<ServiceInstance> chosenInstances = instances;
        chosenInstances = this.filterVersionServiceInstances(instances, headers);
        chosenInstances = this.filterTagServiceInstances(chosenInstances, headers);
        ServiceInstance instance = loadBalancer.choose(chosenInstances);
        return Objects.isNull(instance) ? new EmptyResponse() : new DefaultResponse(instance);
    }

    /**
     * 筛选满足 version 条件的实例列表
     *
     * @param instances 服务实例列表
     * @param headers   请求头
     * @return 筛选后的服务实例列表
     */
    protected List<ServiceInstance> filterVersionServiceInstances(List<ServiceInstance> instances, HttpHeaders headers) {
        String version = headers.getFirst(VERSION);
        if (StringUtils.hasLength(version)) {
            List<ServiceInstance> filteredInstances = instances.stream()
                    .filter(instance -> version.equals(instance.getMetadata().get(VERSION)))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(filteredInstances)) {
                return filteredInstances;
            } else {
                if (log.isInfoEnabled()) {
                    log.info(String.format("Service[%s] does not have instances that match the version (%s), using the full instance list instead.", serviceId, version));
                }
            }
        }
        return instances;
    }

    /**
     * 筛选满足 tag 条件的实例列表
     *
     * @param instances 服务实例列表
     * @param headers   请求头
     * @return 筛选后的服务实例列表
     */
    protected List<ServiceInstance> filterTagServiceInstances(List<ServiceInstance> instances, HttpHeaders headers) {
        // 情况一：没有 tag 时，过滤掉有 tag 的节点
        String tag = headers.getFirst(TAG);
        if (!StringUtils.hasLength(tag)) {
            List<ServiceInstance> filteredInstances = instances.stream()
                    .filter(instance -> !StringUtils.hasLength(instance.getMetadata().get(TAG)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(filteredInstances)) {
                if (log.isInfoEnabled()) {
                    log.info(String.format("Service[%s] does not have instances without tag, using the full instance list instead.", serviceId));
                }
                return instances;
            }
            return filteredInstances;
        }

        // 情况二：有 tag 时，使用 tag 匹配服务实例
        List<ServiceInstance> filteredInstances = instances.stream()
                .filter(instance -> tag.equals(instance.getMetadata().get(TAG)))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(filteredInstances)) {
            if (log.isWarnEnabled()) {
                log.warn(String.format("Service[%s] does not have instances that match the tag (%s), using the full instance list instead.", serviceId, tag));
            }
            return instances;
        }
        return filteredInstances;
    }
}
