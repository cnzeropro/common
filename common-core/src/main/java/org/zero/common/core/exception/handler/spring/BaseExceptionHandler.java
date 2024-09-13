package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.extension.xss.XssException;
import org.zero.common.data.model.vo.Result;

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
public class BaseExceptionHandler {
    /* *************************************************** 系统自定义异常 *************************************************** */
    @ExceptionHandler(org.zero.common.data.exception.UtilException.class)
    public Result<Void> utilException(org.zero.common.data.exception.UtilException e) {
        log.error("Util class method call error", e);
        return Result.fail("系统内部错误，请联系管理员");
    }

    @ExceptionHandler(XssException.class)
    public Result<Void> xssException(XssException e) {
        log.error("There is a risk of XSS (Cross Site Scripting)", e);
        return Result.fail("文本存在跨站脚本攻击，请检查");
    }

    @ExceptionHandler(org.zero.common.data.exception.BaseException.class)
    public Result<Void> baseException(org.zero.common.data.exception.BaseException e) {
        log.error("System base exception", e);
        return Result.fail(e.getPromptMessage(), e.getSysError());
    }
}
