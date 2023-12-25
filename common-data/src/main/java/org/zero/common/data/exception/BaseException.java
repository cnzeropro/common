package org.zero.common.data.exception;

import lombok.Getter;
import org.zero.common.data.enumeration.BaseSysError;
import org.zero.common.data.enumeration.SysError;

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

    public BaseException() {
        this((String) null);
    }

    public BaseException(String message) {
        this(message, message);
    }

    public BaseException(BaseSysError sysError) {
        this(sysError.getMessage(), sysError);
    }

    public BaseException(String message, String promptMessage) {
        this(message, promptMessage, SysError.ERROR);
    }

    public BaseException(String message, BaseSysError sysError) {
        this(message, message, sysError);
    }

    public BaseException(String message, String promptMessage, BaseSysError sysError) {
        super(message);
        this.promptMessage = promptMessage;
        this.sysError = sysError;
    }

    public BaseException(Throwable cause) {
        this(SysError.ERROR, cause);
    }

    public BaseException(String message, Throwable cause) {
        this(message, SysError.ERROR, cause);
    }

    public BaseException(BaseSysError sysError, Throwable cause) {
        this(sysError.getMessage(), sysError, cause);
    }

    public BaseException(String message, String promptMessage, Throwable cause) {
        this(message, promptMessage, SysError.ERROR, cause);
    }

    public BaseException(String message, BaseSysError sysError, Throwable cause) {
        this(message, message, sysError, cause);
    }

    public BaseException(String message, String promptMessage, BaseSysError sysError, Throwable cause) {
        super(message, cause);
        this.promptMessage = promptMessage;
        this.sysError = sysError;
    }

    protected BaseException(String message, String promptMessage, BaseSysError sysError, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.promptMessage = promptMessage;
        this.sysError = sysError;
    }

    public String getErrorCode() {
        return sysError.getCode();
    }

    public String getErrorMessage() {
        return sysError.getMessage();
    }
}
