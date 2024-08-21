package org.zero.common.data.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.zero.common.data.enumeration.BaseSysError;
import org.zero.common.data.enumeration.SysError;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2018/11/29
 */
@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public final class Result<T> implements Serializable {
    private static final long serialVersionUID = 7893804841950761019L;

    public static final int OK_CODE = HttpStatus.OK.value();
    public static final int ERROR_CODE = OK_CODE;
    public static final int FAIL_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public static final String OK_MSG = "操作成功";
    public static final String ERROR_MSG = "操作失败";
    public static final String FAIL_MSG = "请求错误";

    /**
     * 状态码
     */
    private int code;
    /**
     * 用户提示信息
     */
    private String message;
    /**
     * 错误信息
     */
    private BaseSysError error;

    /**
     * 成功标志
     */
    private boolean success;
    /**
     * 时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();

    /**
     * 数据对象
     */
    private T data;

    /* ******************************************************** 请求成功 ******************************************************** */
    public static <T> Result<T> ok() {
        return ok((T) null);
    }

    public static <T> Result<T> ok(T data) {
        return ok(OK_MSG, data);
    }

    public static <T> Result<T> ok(String message) {
        return ok(message, null);
    }

    public static <T> Result<T> ok(String message, T data) {
        return of(OK_CODE, message, SysError.OK, data);
    }

    /* ******************************************************** 请求成功但没有达到预期响应 ******************************************************** */
    public static <T> Result<T> error() {
        return error(ERROR_MSG);
    }

    public static <T> Result<T> error(String message) {
        return error(message, SysError.ERROR);
    }

    public static <T> Result<T> error(T data) {
        return error(ERROR_MSG, data);
    }

    public static <T> Result<T> error(String message, T data) {
        return error(message, SysError.ERROR, data);
    }

    public static <T> Result<T> error(String message, BaseSysError error) {
        return error(message, error, null);
    }

    public static <T> Result<T> error(String message, BaseSysError error, T data) {
        return of(ERROR_CODE, message, error, data);
    }

    /* ******************************************************** 请求失败 ******************************************************** */
    public static <T> Result<T> fail() {
        return fail(FAIL_MSG);
    }

    public static <T> Result<T> fail(String message) {
        return fail(message, SysError.ERROR);
    }

    public static <T> Result<T> fail(String message, BaseSysError error) {
        return fail(FAIL_CODE, message, error);
    }

    public static <T> Result<T> fail(int code, String message) {
        return fail(code, message, SysError.ERROR);
    }

    public static <T> Result<T> fail(int code, String message, BaseSysError error) {
        return of(code, message, error, null);
    }

    /* ******************************************************** 通用构造 ******************************************************** */

    public static <T> Result<T> of(int code, String message, BaseSysError error, T data) {
        return of(code, message, error, LocalDateTime.now(), data);
    }

    public static <T> Result<T> of(int code, String message, BaseSysError error, LocalDateTime time, T data) {
        boolean success = OK_CODE == code;
        if (Objects.nonNull(error)) {
            success = success && SysError.OK.getCode().equals(error.getCode());
        }
        return of(code, message, error, success, time, data);
    }

    public static <T> Result<T> of(int code, String message, BaseSysError error, boolean success, LocalDateTime time, T data) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .error(error)
                .success(success)
                .time(time)
                .data(data)
                .build();
    }
}
