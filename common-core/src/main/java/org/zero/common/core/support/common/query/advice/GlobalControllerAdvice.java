package org.zero.common.core.support.common.query.advice;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/4
 */
@ControllerAdvice
public class GlobalControllerAdvice {
    /**
     * 在每个控制器方法调用之前执行，用于初始化数据绑定器
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    }
}
