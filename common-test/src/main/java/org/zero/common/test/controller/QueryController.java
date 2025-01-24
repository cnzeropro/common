package org.zero.common.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.aop.aspect.log.LogLevel;
import org.zero.common.core.aop.aspect.log.TraceLog;
import org.zero.common.core.extension.spring.webmvc.DynamicBean;
import org.zero.common.data.model.query.BaseQO;
import org.zero.common.data.model.view.Result;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@Slf4j
@RestController
@RequestMapping("query")
public class QueryController {
    @TraceLog(level = LogLevel.INFO)
    @GetMapping("q1")
    public Result<BaseQO> q1(@Validated BaseQO param) {
        return Result.ok(param);
    }

    @GetMapping("q2")
    public Result<DynamicBean> q2(DynamicBean param) {
        return Result.ok(param);
    }
}
