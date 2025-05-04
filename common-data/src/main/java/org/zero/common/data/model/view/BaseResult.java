package org.zero.common.data.model.view;

import org.zero.common.data.constant.HttpStatus;
import org.zero.common.data.exception.BaseSysError;

import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
public interface BaseResult<T> extends org.zero.common.data.model.transfer.BaseResult {
    int OK_CODE = HttpStatus.OK;
    int ERROR_CODE = OK_CODE;
    int FAIL_CODE = HttpStatus.INTERNAL_SERVER_ERROR;

    String OK_MSG = "操作成功";
    String ERROR_MSG = "操作错误";
    String FAIL_MSG = "操作失败";

    int getCode();

    CharSequence getMessage();

    BaseSysError getError();

    LocalDateTime getTime();

    T getData();
}
