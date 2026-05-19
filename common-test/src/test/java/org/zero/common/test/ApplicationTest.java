package org.zero.common.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.zero.common.core.extension.spring.beans.factory.support.CustomBeanNameGenerator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class ApplicationTest {
	@Test
	void shouldDeclareApplicationAnnotations() throws NoSuchMethodException {
		SpringBootApplication springBootApplication = Application.class.getAnnotation(SpringBootApplication.class);
		EnableFeignClients enableFeignClients = Application.class.getAnnotation(EnableFeignClients.class);

		assertNotNull(springBootApplication);
		assertEquals(CustomBeanNameGenerator.class, springBootApplication.nameGenerator());
		assertNotNull(enableFeignClients);
		assertArrayEquals(new String[]{"org.zero.common.api"}, enableFeignClients.basePackages());
		assertNotNull(Application.class.getMethod("main", String[].class));
	}
}
