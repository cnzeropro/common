package org.zero.common.data.model.vo;

import org.springframework.http.HttpStatus;
import org.zero.common.data.model.bo.BaseSysError;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
public interface BaseResult<T> extends Serializable {
    int OK_CODE = HttpStatus.OK.value();
    int ERROR_CODE = OK_CODE;
    int FAIL_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();

    String OK_MSG = "操作成功";
    String ERROR_MSG = "操作错误";
    String FAIL_MSG = "操作失败";

    int getCode();

    String getMessage();

    BaseSysError getError();

    LocalDateTime getTime();

    T getData();

    boolean isSuccess();
}
