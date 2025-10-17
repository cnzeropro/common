package org.zero.common.test.controller;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.extension.common.query.converter.StringToFieldConverter;
import org.zero.common.core.extension.common.query.converter.StringToOperatorConverter;
import org.zero.common.core.extension.spring.web.method.support.JsonObjectArgumentResolvers;
import org.zero.common.core.extension.spring.web.method.support.JsonObjectParam;
import org.zero.common.core.support.bean.dynamic.DynamicBean;
import org.zero.common.core.support.bean.dynamic.DynamicBeanArgumentResolver;
import org.zero.common.core.support.bean.dynamic.DynamicBeanParam;
import org.zero.common.data.model.query.BaseQO;
import org.zero.common.data.model.view.Result;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@Slf4j
@RestController
@RequestMapping("query")
public class QueryController {
    /**
     * @see StringToFieldConverter
     * @see StringToOperatorConverter
     */
    @GetMapping("q1")
    public Result<BaseQO> q1(@Validated BaseQO param) {
        return Result.ok(param);
    }

    /**
     * @see DynamicBeanArgumentResolver
     */
    @GetMapping("q2")
    public Result<DynamicBean> q2(@DynamicBeanParam DynamicBean param) {
        Collection<String> collection = param.getCollection("a", String.class);
        log.info("collection: {}", collection);
        int i = param.getInt("b");
        log.info("i: {}", i);
        Integer[] array = param.getArray("c", Integer.class);
        log.info("array: {}", Arrays.toString(array));
        return Result.ok(param);
    }

    /**
     * @see JsonObjectArgumentResolvers.HutoolJsonArgumentResolver
     */
    @GetMapping("q3")
    public Result<JSONObject> q3(@JsonObjectParam JSONObject param) {
        List<Integer> beanList = param.getBeanList("c", Integer.class);
        log.info("beanList: {}", beanList);
        return Result.ok(param);
    }

    /**
     * @see JsonObjectArgumentResolvers.JacksonArgumentResolver
     */
    @GetMapping("q4")
    public Result<ObjectNode> q4(@JsonObjectParam ObjectNode param) {
        ArrayNode arrayNode = param.withArray("a");
        log.info("arrayNode: {}", arrayNode);
        int i = param.get("b").asInt();
        log.info("i: {}", i);
        JsonNode at = param.at("/c/0");
        log.info("at: {}", at);
        return Result.ok(param);
    }
}
