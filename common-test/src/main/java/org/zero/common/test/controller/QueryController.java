package org.zero.common.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.aop.aspect.log.LogLevel;
import org.zero.common.core.aop.aspect.log.TraceLog;
import org.zero.common.core.util.spring.SpringUtils;
import org.zero.common.data.model.query.BaseQO;
import org.zero.common.data.model.view.Result;

import javax.annotation.PostConstruct;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@Slf4j
@RestController
@RequestMapping("query")
public class QueryController {
    @PostConstruct
    public void init() {
         BeanFactory beanFactory = SpringUtils.getBeanFactory();

        String appName = SpringUtils.getAppName();
        log.info("appName: {}", appName);
    }

    @TraceLog(level = LogLevel.INFO)
    @GetMapping("q1")
    public Result<BaseQO> q1(@Validated BaseQO param) {
        return Result.ok(param);
    }
}
