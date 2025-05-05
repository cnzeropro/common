package org.zero.common.core.extension.spring.web.bind;

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
        // 所有控制器中禁止绑定 version、deleted 字段
        binder.setDisallowedFields("version", "deleted");
    }
}
