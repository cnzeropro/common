package org.zero.common.log.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zero
 * @since 2022/8/25
 */
@UtilityClass
public class LogUtil {
    private final Pattern pattern = Pattern.compile("(?<!\\\\)\\{([^{}]+)(?<!\\\\)}");

    public String getMessage(String messageTemplate, Object... beans) {
        Map<String, Object> map = MapUtil.newHashMap();
        for (Object bean : beans) {
            BeanUtil.beanToMap(bean, map, false, true);
        }
        return getMessage(messageTemplate, map);
    }

    public String getMessage(String messageTemplate, Object bean) {
        String message = messageTemplate;
        Matcher matcher = pattern.matcher(messageTemplate);
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = BeanUtil.getProperty(bean, key);
            if (Objects.nonNull(value)) {
                message = message.replace("{" + key + "}", StrUtil.utf8Str(value));
            }
        }
        return message;
    }
}
