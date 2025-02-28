package org.zero.common.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.aop.aspect.log.LogLevel;
import org.zero.common.core.aop.aspect.log.LogTracker;
import org.zero.common.core.extension.spring.webmvc.DynamicBean;
import org.zero.common.core.extension.spring.webmvc.DynamicBeanParam;
import org.zero.common.data.model.query.BaseQO;
import org.zero.common.data.model.query.PageQO;
import org.zero.common.data.model.view.Result;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@Slf4j
@RestController
@RequestMapping("query")
public class QueryController {
    /**
     * http://127.0.0.1:34567/query/q1?fields=id,name
     */
    @LogTracker(LogLevel.INFO)
    @GetMapping("q1")
    public Result<BaseQO> q1(@Validated PageQO param) {
        return Result.ok(param);
    }

    /**
     * http://127.0.0.1:34567/query/q2?a=nnn&a=mmm&b=154&c=123,456
     *
     * @see org.zero.common.test.config.AppConfig#converterInit()
     */
    @GetMapping("q2")
    public Result<DynamicBean> q2(@DynamicBeanParam DynamicBean param) {
        Collection<Integer> collection = param.getCollection("c", Integer.class);
        log.info("collection: {}", collection);
        int i = param.getInt("b");
        log.info("i: {}", i);
        Integer[] array = param.getArray("c", Integer.class);
        log.info("array: {}", Arrays.toString(array));
        return Result.ok(param);
    }
}
