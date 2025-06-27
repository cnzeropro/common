package org.zero.common.test.feature.bean.obtain;

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
    final ApplicationContextWay applicationContextWay;
    final AutowiredAnnotation autowiredAnnotation;
    final BeanFactoryWay beanFactoryWay;
    final ConstructorWay constructorWay;
    final InjectAnnotation injectAnnotation;
    final LookupAnnotation lookupAnnotation;
    final ObjectFactoryWay objectFactoryWay;
    final ObjectProviderWay objectProviderWay;
    final OptionalWay optionalWay;
    final ResourceAnnotation resourceAnnotation;
    final UtilWay utilWay;
    final ValueAnnotation valueAnnotation;

    /**
     * test
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("ApplicationContextWay environment: {}", applicationContextWay.environment);
        log.info("AutowiredAnnotation environment: {}", autowiredAnnotation.environment);
        log.info("BeanFactoryWay environment: {}", beanFactoryWay.environment);
        log.info("ConstructorWay environment: {}", constructorWay.environment);
        log.info("InjectAnnotation environment: {}", injectAnnotation.environment);
        log.info("LookupAnnotation environment: {}", lookupAnnotation.getEnvironment());
        log.info("ObjectFactoryWay environment: {}", objectFactoryWay.environment);
        log.info("ObjectProviderWay environment: {}", objectProviderWay.environment);
        log.info("OptionalWay environment: {}", optionalWay.environment);
        log.info("ResourceAnnotation environment: {}", resourceAnnotation.environment);
        log.info("UtilWay environment: {}", utilWay.environment);
        log.info("ValueAnnotation environment: {}", valueAnnotation.environment);
    }
}
