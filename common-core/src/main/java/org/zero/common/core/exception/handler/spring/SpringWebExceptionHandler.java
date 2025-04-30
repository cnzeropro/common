package org.zero.common.core.exception.handler.spring;

import cn.hutool.core.io.unit.DataSizeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.data.model.view.Result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 异常处理器
 * <p>
 * 异常建议从小到大（便于代码阅读和后期维护）
 *
 * @author Zero
 * @since 2020/03/21
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication
public class SpringWebExceptionHandler extends AbstractThrowableHandler {
    public SpringWebExceptionHandler(ThrowableMessageProvider throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** Web 异常 *************************************************** */
    /* ################# org.springframework.web.server.ResponseStatusException ################# */
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Result<Void>> responseStatusException(org.springframework.web.server.ResponseStatusException e) {
        log.error(String.format("Response failed (%d)", e.getRawStatusCode()), e);
        HttpStatus httpStatus = e.getStatus();
        Result<Void> result = this.handleThrowable(httpStatus, e, e.getReason());
        return ResponseEntity.status(httpStatus)
                .body(result);
    }

    /* ################# org.springframework.web.client.RestClientException ################# */
    @ExceptionHandler(org.springframework.web.client.HttpStatusCodeException.class)
    public ResponseEntity<Result<Void>> httpStatusCodeException(org.springframework.web.client.HttpStatusCodeException e) {
        log.error("RestTemplate request failed", e);
        HttpStatus httpStatus = e.getStatusCode();
        Result<Void> result = this.handleThrowable(httpStatus, e, e.getStatusText());
        return ResponseEntity.status(httpStatus)
                .body(result);
    }

    @ExceptionHandler(org.springframework.web.client.UnknownHttpStatusCodeException.class)
    public Result<Void> unknownHttpStatusCodeException(org.springframework.web.client.UnknownHttpStatusCodeException e) {
        log.error("RestTemplate response unknown http status code", e);
        return this.handleThrowable(e.getRawStatusCode(), e, e.getStatusText());
    }

    @ExceptionHandler(org.springframework.web.client.UnknownContentTypeException.class)
    public Result<Void> unknownContentTypeException(org.springframework.web.client.UnknownContentTypeException e) {
        log.error("RestTemplate unknown content type", e);
        return this.handleThrowable(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e, e.getStatusText());
    }

    @ExceptionHandler(org.springframework.web.client.ResourceAccessException.class)
    public Result<Void> resourceAccessException(org.springframework.web.client.ResourceAccessException e) {
        log.error("RestTemplate resource access failed", e);
        return this.handleThrowable(e);
    }

    /* ################# org.springframework.web.bind.MissingRequestValueException ################# */
    @ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
    public Result<Void> missingRequestHeaderException(org.springframework.web.bind.MissingRequestHeaderException e) {
        String headerName = e.getHeaderName();
        log.error(String.format("Missing request header: %s", headerName), e);
        return this.handleThrowable(HttpStatus.EXPECTATION_FAILED, e, new Object[]{headerName});
    }

    @ExceptionHandler(org.springframework.web.bind.MissingRequestCookieException.class)
    public Result<Void> missingRequestCookieException(org.springframework.web.bind.MissingRequestCookieException e) {
        String cookieName = e.getCookieName();
        log.error(String.format("Missing cookie: %s", cookieName), e);
        return this.handleThrowable(e, Collections.singletonList(cookieName));
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public Result<Void> missingServletRequestParameterException(org.springframework.web.bind.MissingServletRequestParameterException e) {
        String parameterName = e.getParameterName();
        log.error(String.format("Missing request parameter: %s (%s)", parameterName, e.getParameterType()), e);
        return this.handleThrowable(e, Collections.singletonList(parameterName));
    }

    @ExceptionHandler(org.springframework.web.bind.MissingPathVariableException.class)
    public Result<Void> missingPathVariableException(org.springframework.web.bind.MissingPathVariableException e) {
        String variableName = e.getVariableName();
        log.error(String.format("Missing path variable: %s", variableName), e);
        return this.handleThrowable(e, Collections.singletonList(variableName));
    }

    @ExceptionHandler(org.springframework.web.bind.MissingMatrixVariableException.class)
    public Result<Void> missingMatrixVariableException(org.springframework.web.bind.MissingMatrixVariableException e) {
        String variableName = e.getVariableName();
        log.error(String.format("Missing matrix variable: %s", variableName), e);
        return this.handleThrowable(e, Collections.singletonList(variableName));
    }

    /* ################# org.springframework.web.multipart.support.MissingServletRequestPartException ################# */
    @ExceptionHandler(org.springframework.web.multipart.support.MissingServletRequestPartException.class)
    public Result<Void> missingServletRequestPartException(org.springframework.web.multipart.support.MissingServletRequestPartException e) {
        String requestPartName = e.getRequestPartName();
        log.error(String.format("Missing request part: %s", requestPartName), e);
        return this.handleThrowable(e, Collections.singletonList(requestPartName));
    }

    /* ################# org.springframework.web.HttpSessionRequiredException ################# */
    @ExceptionHandler(org.springframework.web.HttpSessionRequiredException.class)
    public Result<Void> httpSessionRequiredException(org.springframework.web.HttpSessionRequiredException e) {
        String expectedAttribute = e.getExpectedAttribute();
        log.error(String.format("Http session expected: %s", expectedAttribute), e);
        return this.handleThrowable(HttpStatus.EXPECTATION_FAILED, e, Collections.singletonList(expectedAttribute));
    }

    /* ################# org.springframework.web.HttpRequestMethodNotSupportedException ################# */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public Result<Void> httpRequestMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException e) {
        String method = e.getMethod();
        log.error(String.format("The request method[%s] is not supported, only supported: %s", method, Arrays.toString(e.getSupportedMethods())), e);
        return this.handleThrowable(HttpStatus.METHOD_NOT_ALLOWED, e, Collections.singletonList(method));
    }

    /* ################# org.springframework.web.HttpMediaTypeException ################# */
    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public Result<Void> httpMediaTypeNotSupportedException(org.springframework.web.HttpMediaTypeNotSupportedException e) {
        MediaType contentType = e.getContentType();
        log.error(String.format("The media type[%s] is not supported, only supported: %s", contentType, e.getSupportedMediaTypes()), e);
        return this.handleThrowable(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e, Collections.singletonList(contentType));
    }

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotAcceptableException.class)
    public Result<Void> httpMediaTypeNotAcceptableException(org.springframework.web.HttpMediaTypeNotAcceptableException e) {
        log.error(String.format("Media type is not acceptable, only supported: %s", e.getSupportedMediaTypes()), e);
        return this.handleThrowable(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e);
    }

    /* ################# org.springframework.web.multipart.MultipartException ################# */
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public Result<Void> maxUploadSizeExceededException(org.springframework.web.multipart.MaxUploadSizeExceededException e) {
        String size = DataSizeUtil.format(e.getMaxUploadSize());
        log.error(String.format("The uploaded file exceeds the specified size: %s", size), e);
        return this.handleThrowable(HttpStatus.PAYLOAD_TOO_LARGE, e, Collections.singletonList(size));
    }

    /* ################# org.springframework.web.bind.MethodArgumentNotValidException ################# */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Result<Void> methodArgumentNotValidException(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String errorMsg = Optional.of(e.getAllErrors())
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(" | ", "[", "]"));
        log.error(String.format("The request parameter validation is abnormal: %s", errorMsg), e);
        return this.handleThrowable(HttpStatus.BAD_REQUEST, e, Collections.singletonList(errorMsg));
    }

    /* ################# org.springframework.http.converter.HttpMessageConversionException ################# */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public Result<Void> httpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException e) {
        log.error("Error parameter", e);
        return this.handleThrowable(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotWritableException.class)
    public Result<Void> httpMessageNotWritableException(org.springframework.http.converter.HttpMessageNotWritableException e) {
        log.error("Error result", e);
        return this.handleThrowable(HttpStatus.BAD_REQUEST, e);
    }
}
