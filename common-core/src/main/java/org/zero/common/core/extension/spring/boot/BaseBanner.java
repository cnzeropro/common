package org.zero.common.core.extension.spring.boot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
@FunctionalInterface
public interface BaseBanner extends ApplicationRunner, Ordered {
    @Override
    default void run(ApplicationArguments args) throws Exception {
        // 延迟 500 毫秒，使其输出到尾部
        Thread.sleep(500);
        this.print(args);
    }

    void print(ApplicationArguments args);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
