package org.zero.common.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.aop.aspect.log.LogLevel;
import org.zero.common.core.aop.aspect.log.TraceLog;
import org.zero.common.core.extension.spring.webmvc.DynamicBean;
import org.zero.common.core.extension.spring.webmvc.DynamicBeanArgumentResolver;
import org.zero.common.data.model.query.BaseQO;
import org.zero.common.data.model.view.Result;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@Slf4j
@RestController
@RequestMapping("result")
public class ResultController {
    @TraceLog(level = LogLevel.INFO)
    @GetMapping("r1")
    public Result<BaseQO> r1(@Validated BaseQO param) {
        return Result.ok(param);
    }

    @GetMapping("r2")
    public Result<DynamicBean> r2(@DynamicBeanArgumentResolver.DynamicBeanParam DynamicBean param) {
        param.getString("");
        return Result.ok(param);
    }
}
