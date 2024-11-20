package org.zero.common.data.exception;

import lombok.Getter;
import org.zero.common.data.model.bo.BaseSysError;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/6/20
 */
@Getter
public class BaseException extends RuntimeException {
    /**
     * 用户提示消息
     */
    protected final String promptMessage;
    /**
     * 系统错误信息
     */
    protected final BaseSysError sysError;

    /* ************************************************************** Exception() ************************************************************** */
    public BaseException() {
        this(BaseSysError.DefaultSysError.ERROR);
    }

    public BaseException(BaseSysError sysError) {
        this(sysError.getMessage(), sysError);
    }

    /* ************************************************************** Exception(java.lang.String) ************************************************************** */
    public BaseException(String message) {
        this(message, message);
    }

    public BaseException(String message, String promptMessage) {
        this(message, promptMessage, BaseSysError.DefaultSysError.ERROR);
    }

    public BaseException(String message, BaseSysError sysError) {
        this(message, message, sysError);
    }

    public BaseException(String message, String promptMessage, BaseSysError sysError) {
        super(message);
        this.promptMessage = promptMessage;
        this.sysError = sysError;
    }

    /* ************************************************************** Exception(java.lang.Throwable) ************************************************************** */
    public BaseException(Throwable cause) {
        this(cause, BaseSysError.DefaultSysError.ERROR);
    }

    public BaseException(Throwable cause, String promptMessage) {
        this(cause, promptMessage, BaseSysError.DefaultSysError.ERROR);
    }

    public BaseException(Throwable cause, BaseSysError sysError) {
        this(cause, sysError.getMessage(), sysError);
    }

    public BaseException(Throwable cause, String promptMessage, BaseSysError sysError) {
        super(cause);
        this.promptMessage = promptMessage;
        this.sysError = sysError;
    }

    /* ************************************************************** Exception(java.lang.String, java.lang.Throwable) ************************************************************** */
    public BaseException(String message, Throwable cause) {
        this(message, cause, message);
    }

    public BaseException(String message, Throwable cause, String promptMessage) {
        this(message, cause, promptMessage, BaseSysError.DefaultSysError.ERROR);
    }

    public BaseException(String message, Throwable cause, BaseSysError sysError) {
        this(message, cause, message, sysError);
    }

    public BaseException(String message, Throwable cause, String promptMessage, BaseSysError sysError) {
        super(message, cause);
        this.promptMessage = promptMessage;
        this.sysError = sysError;
    }

    /* ************************************************************** other ************************************************************** */
    public String getErrorCode() {
        return sysError.getCode();
    }

    public String getErrorMessage() {
        return sysError.getMessage();
    }
}
