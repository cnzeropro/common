package org.zero.common.core.support.mybatis;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2021/8/12
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.CHAR, JdbcType.VARCHAR, JdbcType.LONGVARCHAR})
public class HutoolJsonTypeHandler extends BaseJsonTypeHandler<Object> {
    private final Class<?> propertyType;

    private static JSONConfig jsonConfig;

    /**
     * @see org.apache.ibatis.type.TypeHandlerRegistry#getInstance(Class, Class)
     */
    public HutoolJsonTypeHandler(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    protected Object parseJson(String json) {
        try {
            return JSONUtil.parse(json, getJsonConfig()).toBean(propertyType);
        } catch (Exception e) {
            log.warn(String.format("Can not parse json string: %s", json), e);
            return null;
        }
    }

    @Override
    protected String toJson(Object object) {
        try {
            return JSONUtil.toJsonStr(object, getJsonConfig());
        } catch (Exception e) {
            log.warn(String.format("Can not convert object to Json: %s", object), e);
            return null;
        }
    }

    protected static JSONConfig getJsonConfig() {
        if (Objects.isNull(jsonConfig)) {
            jsonConfig = JSONConfig.create();
        }
        return jsonConfig;
    }

    public static void setJsonConfig(JSONConfig jsonConfig) {
        HutoolJsonTypeHandler.jsonConfig = jsonConfig;
    }
}
