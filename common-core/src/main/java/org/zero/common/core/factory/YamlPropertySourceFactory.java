package org.zero.common.core.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Properties;

/**
 * 读取 yaml 文件的工厂类
 * <p>
 * 使用方式：
 * <pre>
 *     {@code
 *     @org.springframework.context.annotation.PropertySource(value = "classpath:/config/app.yml", factory = YamlPropertySourceFactory.class)
 *     public class ApplicationConfig {}
 *     }
 * </pre>
 *
 * @author zero
 * @since 2021/2/12
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) {
        Resource r = resource.getResource();
        String sourceName = Objects.nonNull(name) ? name : r.getFilename();
        Properties properties = loadYamlToProperties(r);
        Assert.notNull(sourceName, "Source name is null");
        Assert.notNull(properties, "Source properties is null");
        return new PropertiesPropertySource(sourceName, properties);
    }

    private Properties loadYamlToProperties(Resource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
