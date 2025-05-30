package org.zero.common.test.feature.annotation.obscure;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;

/**
 * {@linkplain ConfigurationPropertiesBinding @ConfigurationPropertiesBinding} 使容器在绑定属性值时能通过转换器进行处理
 * <p>
 * 使用配置如下：
 * <pre>{@code
 * test:
 *   common:
 *     name: test
 *     bean: "zero;324325346;2023-09-01 09:09:09.000"
 * }</pre>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
@Component
class ConfigurationPropertiesBindingAnnotation {
    @Data
    @Component
    @ConfigurationProperties(prefix = "test.common")
    static class CommonProperties {
        private String name;
        private Bean bean;
    }

    @Component
    @ConfigurationPropertiesBinding
    static class BeanConverter implements Converter<String, Bean> {
        @SneakyThrows
        @Override
        public Bean convert(String source) {
            String[] values = source.split(";");
            if (values.length >= 3) {
                Bean bean = new Bean();
                bean.setString(values[0]);
                bean.setInteger(Integer.valueOf(values[1]));
                bean.setDate(DateFormat.getDateTimeInstance().parse(values[2]));
                return bean;
            }
            return null;
        }
    }

    @Data
    static class Bean {
        private String string;
        private Integer integer;
        private Date date;
    }
}
