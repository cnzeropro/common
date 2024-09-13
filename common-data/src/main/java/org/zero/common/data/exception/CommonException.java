package org.zero.common.data.exception;

import org.zero.common.data.enumeration.BaseSysError;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/6/20
 */
public class CommonException extends BaseException {
    public CommonException() {
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonException(String message, String promptMessage) {
        super(message, promptMessage);
    }

    public CommonException(String message, String promptMessage, Throwable cause) {
        super(message, promptMessage, cause);
    }

    public CommonException(String message, String promptMessage, BaseSysError sysError) {
        super(message, promptMessage, sysError);
    }

    public CommonException(String message, String promptMessage, BaseSysError sysError, Throwable cause) {
        super(message, promptMessage, sysError, cause);
    }
}
