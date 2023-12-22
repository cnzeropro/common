package org.zero.common.swagger.annotation;

import org.springframework.context.annotation.Import;
import org.zero.common.swagger.config.SwaggerConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zero
 * @date 2022/6/7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SwaggerConfiguration.class})
public @interface EnableSwagger {
}