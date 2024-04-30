package org.zero.common.log.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zero.common.log.aspect.AutoLogAspect;
import org.zero.common.log.event.SysLogListener;
import org.zero.common.log.feign.RemoteLogService;

/**
 * 日志自动装配
 *
 * @author zero
 * @date 2022/10/1
 */
@Configuration(proxyBeanMethods = false)
public class LogAutoConfiguration {

    /**
     * 注入日志监听器
     */
    @Bean
    SysLogListener sysLogListener(RemoteLogService remoteLogService) {
        return new SysLogListener(remoteLogService);
    }

    /**
     * 注入日志切面
     */
    @Bean
    AutoLogAspect sysLogAspect() {
        return new AutoLogAspect();
    }
}
