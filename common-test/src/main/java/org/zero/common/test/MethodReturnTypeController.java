package org.zero.common.test;

import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.view.InternalResourceView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * 控制器方法返回值处理器支持的类型测试
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.web.method.support.HandlerMethodReturnValueHandler
 * @see org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#getDefaultReturnValueHandlers()
 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addReturnValueHandlers(java.util.List)
 * @since 2025/4/22
 */
@Controller
@RequestMapping("/return")
public class MethodReturnTypeController {
    /* ********************************************************* sync ********************************************************* */

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler ****************************** */

    /**
     * 参见 {@link CharSequence} 的派生类
     */
    @RequestMapping("/charSequence")
    public CharSequence charSequence() {
        return "charSequence";
    }

    /**
     * 返回 {@code void} 类型，会根据请求的 URL 路径自动推断视图。
     * 参见：{@linkplain org.springframework.web.servlet.RequestToViewNameTranslator RequestToViewNameTranslator}。
     * 如果使用 {@linkplain ResponseBody @ResponseBody} 返回数据，则表示返回空响应体，不涉及视图解析。
     * 另外，控制器方法体中如果设置了请求转发或重定向，也可返回 {@code void} 类型
     */
    @RequestMapping("/void")
    public void none() {
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler ****************************** */

    /**
     * 参见 {@link View} 的派生类
     */
    @RequestMapping("/view")
    public View view() {
        return new InternalResourceView();
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler ****************************** */

    /**
     * 参见 {@link ModelAndView} 及其派生类
     */
    @RequestMapping("/modelAndView")
    public ModelAndView modelAndView() {
        return new ModelAndView("modelAndView");
    }

    /* ****************************** org.springframework.web.method.annotation.MapMethodProcessor ****************************** */

    /**
     * 参见 {@link Map} 的派生类
     */
    @RequestMapping("/map")
    public Map<String, Object> map() {
        return Collections.singletonMap("test", 123);
    }

    /**
     * 参见 {@link ModelMap} 及其派生类
     * <p>
     * {@link ModelMap} 本就是 {@link Map} 的派生类，此处单独列举，是因为  {@link ModelMap} 在 {@code Spring MVC} 中具有含义
     */
    @RequestMapping("/modelMap")
    public ModelMap modelMap() {
        return new ModelMap("test", 123);
    }

    /* ****************************** org.springframework.web.method.annotation.ModelMethodProcessor ****************************** */

    /**
     * 参见 {@link Model} 的派生类
     */
    @RequestMapping("/model")
    public Model model() {
        return new ConcurrentModel("test", 123);
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler ****************************** */

    /**
     * 参见 {@link HttpHeaders} 及其派生类
     */
    @RequestMapping("/headers")
    public HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Test", "test");
        return headers;
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor ****************************** */

    /**
     * 参见 {@link HttpEntity} 及其派生类，但从中排除 {@link RequestEntity} 及其派生类
     */
    @RequestMapping("/httpEntity")
    public HttpEntity<String> httpEntity() {
        return ResponseEntity.ok()
                .header("X-Test", "test")
                .body("httpEntity");
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     * <p>
     * 但注意该控制器上或者其方法上必须注解了 {@linkplain ResponseBody @ResponseBody}
     */
    @ResponseBody
    @RequestMapping("/responseBody")
    public Object responseBody() {
        return Collections.singletonList(Collections.singletonMap("test", 123));
    }

    /* ****************************** org.springframework.web.method.annotation.ModelAttributeMethodProcessor ****************************** */

    /**
     * 参见 {@link Object} 及其派生类
     * <p>
     * {@linkplain ModelAttribute @ModelAttribute} 将方法的返回值自动绑定到模型（Model）中，以便在视图（View）中直接使用这些数据。
     * 如果没有指定模型属性名称，则默认使用方法返回值的名称作为模型属性名称。
     * 具体处理逻辑参见：{@linkplain org.springframework.web.method.annotation.ModelFactory#getNameForReturnValue(java.lang.Object, org.springframework.core.MethodParameter) ModelFactory#getNameForReturnValue}。
     */
    @ModelAttribute("test")
    public Object modelAttribute() {
        return Collections.singletonList(Collections.singletonMap("test", 123));
    }

    /* ********************************************************* async ********************************************************* */

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler ****************************** */

    /**
     * 参见 {@link StreamingResponseBody} 的派生类
     */
    @RequestMapping("/streamingResponseBody")
    public StreamingResponseBody streamingResponseBody() {
        InputStream inputStream = new ByteArrayInputStream("streamingResponseBody".getBytes(StandardCharsets.UTF_8));
        return outputStream -> FileCopyUtils.copy(inputStream, outputStream);
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler ****************************** */

    /**
     * 参见 {@link DeferredResult} 及其派生类
     */
    @RequestMapping("/deferredResult")
    public DeferredResult<String> deferredResult() {
        return new DeferredResult<>(1000L, "deferredResult");
    }

    /**
     * 参见 {@link ListenableFuture} 的派生类
     */
    @RequestMapping("/listenableFuture")
    public ListenableFuture<String> listenableFuture() {
        return new AsyncResult<>("listenableFuture");
    }

    /**
     * 参见 {@link CompletionStage} 的派生类
     */
    @RequestMapping("/completionStage")
    public CompletionStage<String> completionStage() {
        return CompletableFuture.completedFuture("completionStage");
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler ****************************** */

    /**
     * 参见 {@link Callable} 的派生类
     */
    @RequestMapping("/callable")
    public Callable<String> callable() {
        return () -> {
            TimeUnit.MILLISECONDS.sleep(1000);
            return "callable";
        };
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler ****************************** */

    /**
     * 参见 {@link ResponseBodyEmitter} 及其派生类
     */
    @SneakyThrows
    @RequestMapping("/responseBodyEmitter")
    public ResponseBodyEmitter responseBodyEmitter() {
        SseEmitter sseEmitter = new SseEmitter();
        for (int i = 0; i < 10; i++) {
            sseEmitter.send(SseEmitter.event()
                    .id("id" + i)
                    .name("name" + i)
                    .data("data" + i, MediaType.TEXT_PLAIN)
                    .comment("comment" + i)
                    .reconnectTime(1000L));
            TimeUnit.MILLISECONDS.sleep(100);
        }
        return sseEmitter;
    }

    /**
     * 参见 {@link ResponseEntity} 及其派生类泛型参数持有 {@link ResponseBodyEmitter} 及其派生类
     */
    @SneakyThrows
    @RequestMapping("/responseBodyEmitterResponseEntity")
    public ResponseEntity<ResponseBodyEmitter> responseBodyEmitterResponseEntity() {
        SseEmitter sseEmitter = new SseEmitter();
        for (int i = 0; i < 10; i++) {
            sseEmitter.send(SseEmitter.event()
                    .id("id" + i)
                    .name("name" + i)
                    .data("data" + i, MediaType.TEXT_PLAIN)
                    .comment("comment" + i)
                    .reconnectTime(1000L));
            TimeUnit.MILLISECONDS.sleep(100);
        }
        return ResponseEntity.ok()
                .header("X-Test", "test")
                .body(sseEmitter);
    }

    /**
     * 参见 {@code ReactiveType}（{@linkplain org.springframework.web.servlet.mvc.method.annotation.ReactiveTypeHandler#isReactiveType(java.lang.Class) ReactiveTypeHandler#isReactiveType}）。
     * 如 {@link Mono}、{@link Flux}、{@link Publisher} 等。
     *
     * @see org.springframework.core.ReactiveAdapterRegistry#ReactiveAdapterRegistry()
     */
    @RequestMapping("/reactiveType")
    public Mono<String> reactiveType() {
        return Mono.just("Mono");
    }

    /* ****************************** org.springframework.web.servlet.mvc.method.annotation.AsyncTaskMethodReturnValueHandler ****************************** */

    /**
     * 参见 {@link WebAsyncTask} 及其派生类
     */
    @RequestMapping("/webAsyncTask")
    public WebAsyncTask<String> webAsyncTask() {
        return new WebAsyncTask<>(1000L,
                new SimpleAsyncTaskExecutor(),
                () -> "webAsyncTask");
    }
}
