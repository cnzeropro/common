package org.zero.common.test.feature.annotation.obscure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
@Slf4j
@RequiredArgsConstructor
@Component
class TestRunner implements CommandLineRunner {
    final LookupAnnotation.BusinessBean businessBean;
    final DeclareParentsAnnotation.CustomService customService;
    final ConfigurationPropertiesBindingAnnotation.CommonProperties commonProperties;

    /**
     * test
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("businessBean: {}", businessBean.getBean());
        log.info("customService: {}", ((DeclareParentsAnnotation.Service) customService).service());
        log.info("commonProperties: {}", commonProperties);
    }
}
