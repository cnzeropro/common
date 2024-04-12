package org.zero.common.core.exception.controller;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;

import java.util.List;

/**
 * 自定义错误控制器
 *
 * @author zero
 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
 * @since 2024/4/12
 */
public class CustomErrorController extends AbstractErrorController {
    public CustomErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    public CustomErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }
}
