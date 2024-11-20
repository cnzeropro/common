package org.zero.common.data.model.bo;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author Zero
 * @since 2021/8/24
 */
public interface BaseSysError extends Serializable {
    String OK_CODE = "00000";
    String ERROR_CODE = "11111";

    String getCode();

    String getMessage();

    default boolean isOk() {
        return OK_CODE.equals(getCode());
    }

    @Getter
    class DefaultSysError implements BaseSysError {
        public static final BaseSysError OK = new DefaultSysError(OK_CODE, "ok");
        public static final BaseSysError ERROR = new DefaultSysError(ERROR_CODE, "error");

        protected final String code;
        protected final String message;

        protected DefaultSysError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public static DefaultSysError of(String code, String message) {
            return new DefaultSysError(code, message);
        }
    }
}
