package org.zero.common.data.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import org.zero.common.data.model.bo.BaseSysError;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/12/1
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SysError implements BaseSysError {
    /**
     * 一切ok
     */
    OK(OK_CODE, "ok"),
    /**
     * 宏观错误
     */
    ERROR(ERROR_CODE, "error"),
    ;

    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
