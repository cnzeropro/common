package org.zero.common.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.data.exception.BaseSysError;

/**
 * 系统错误枚举
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/12/1
 */
@Getter
@RequiredArgsConstructor
public enum SysError implements BaseSysError {
    /**
     * 一切可行
     */
    OK(OK_CODE, "ok"),
    /**
     * 宏观错误
     */
    ERROR(ERROR_CODE, "error"),
    ;

    private final String code;
    private final String message;
}
