package org.zero.common.core.extension.spring.boot;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
@Slf4j
public class CustomBanner implements BaseBanner, EnvironmentAware {
    protected Environment environment;

    @Override
    public void print(ApplicationArguments args) {
        String appName = environment.getProperty("spring.application.name", "unknown");
        String ip = NetUtil.getLocalhostStr();
        String port = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "/");
        log.info("\n----------------------------------------------------------\n"
                        + "\tApplication [{}] is running! Access urls:\n"
                        + "\tLocal: \t\thttp://127.0.0.1:{}{}\n"
                        + "\tExternal: \thttp://{}:{}{}\n"
                        + "----------------------------------------------------------"
                , appName, port, contextPath, ip, port, contextPath);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
