package org.zero.common.api.extra.loki;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/25
 */
public class LokiFeignConfig {
    /**
     * 配置请求重试
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(150, 1500, 3);
    }

    /**
     * 设置请求超时时间
     */
    @Bean
    Request.Options options() {
        return new Request.Options(5, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, true);
    }

    /**
     * 日志级别
     */
    @Bean
    feign.Logger.Level loggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
