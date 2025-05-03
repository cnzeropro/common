package org.zero.common.test;

import lombok.SneakyThrows;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.web.method.support.HandlerMethodReturnValueHandler
 * @since 2025/4/22
 */
@Controller
@RequestMapping("/returning")
public class ControllerReturning {
    /* ********************************************************* sync ********************************************************* */

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler
     */
    @RequestMapping("/modelAndView")
    public ModelAndView modelAndView() {
        return new ModelAndView("modelAndView");
    }

    /**
     * @see org.springframework.web.method.annotation.MapMethodProcessor
     */
    @RequestMapping("/map")
    public Map<String, Object> map() {
        return Collections.singletonMap("test", 123);
    }

    /**
     * @see org.springframework.web.method.annotation.MapMethodProcessor
     */
    @RequestMapping("/modelMap")
    public ModelMap modelMap() {
        return new ModelMap("test", 123);
    }

    /**
     * @see org.springframework.web.method.annotation.ModelMethodProcessor
     */
    @RequestMapping("/model")
    public Model model() {
        return new ConcurrentModel("test", 123);
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler
     */
    @RequestMapping("/charSequence")
    public CharSequence charSequence() {
        return "charSequence";
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler
     */
    @RequestMapping("/void")
    public void v() {

    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler
     */
    @RequestMapping("/view")
    public View view() {
        return new InternalResourceView();
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler
     */
    @RequestMapping("/headers")
    public HttpHeaders headers() {
        return HttpHeaders.EMPTY;
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor
     */
    @RequestMapping("/httpEntity")
    public HttpEntity<String> httpEntity() {
        return ResponseEntity.ok()
                .header("X-Test", "test")
                .body("httpEntity");
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor
     */
    @ResponseBody
    @RequestMapping("/responseBody")
    public Object responseBody() {
        return Collections.singletonList(Collections.singletonMap("test", 123));
    }

    /**
     * @see org.springframework.web.method.annotation.ModelAttributeMethodProcessor
     */
    @ModelAttribute
    @RequestMapping("/modelAttribute")
    public Object modelAttribute() {
        return Collections.singletonList(Collections.singletonMap("test", 123));
    }

    /* ********************************************************* async ********************************************************* */

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler
     */
    @RequestMapping("/streamingResponseBody")
    public StreamingResponseBody streamingResponseBody() {
        InputStream inputStream = new ByteArrayInputStream("streamingResponseBody".getBytes(StandardCharsets.UTF_8));
        return outputStream -> FileCopyUtils.copy(inputStream, outputStream);
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler
     */
    @RequestMapping("/deferredResult")
    public DeferredResult<String> deferredResult() {
        return new DeferredResult<>(1000L, "deferredResult");
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler
     */
    @RequestMapping("/listenableFuture")
    public ListenableFuture<String> listenableFuture() {
        return new AsyncResult<>("listenableFuture");
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler
     */
    @RequestMapping("/completionStage")
    public CompletionStage<String> completionStage() {
        return CompletableFuture.completedFuture("completionStage");
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler
     */
    @RequestMapping("/callable")
    public Callable<String> callable() {
        return () -> {
            TimeUnit.MILLISECONDS.sleep(1000);
            return "callable";
        };
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler
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
                    .reconnectTime(1000L)
                    .build());
            TimeUnit.MILLISECONDS.sleep(100);
        }
        return sseEmitter;
    }

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.AsyncTaskMethodReturnValueHandler
     */
    @RequestMapping("/webAsyncTask")
    public WebAsyncTask<String> webAsyncTask() {
        return new WebAsyncTask<>(1000L,
                new SimpleAsyncTaskExecutor(),
                () -> "webAsyncTask");
    }
}
