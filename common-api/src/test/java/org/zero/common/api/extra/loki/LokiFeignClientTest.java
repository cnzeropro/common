package org.zero.common.api.extra.loki;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.zero.common.api.extra.loki.model.request.LokiConfigRequest;
import org.zero.common.api.extra.loki.model.request.LokiDeleteCancelRequest;
import org.zero.common.api.extra.loki.model.request.LokiDeleteRequest;
import org.zero.common.api.extra.loki.model.request.LokiIngesterShutdownRequest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class LokiFeignClientTest {
	@Test
	void shouldDeclareExpectedEndpointMappings() throws Exception {
		Method logLevelGet = LokiFeignClient.class.getMethod("logLevelGet");
		assertEquals(0, logLevelGet.getParameterCount());
		assertArrayEquals(new String[]{"/log_level"}, logLevelGet.getAnnotation(GetMapping.class).value());

		Method configWithMode = LokiFeignClient.class.getMethod("config", LokiConfigRequest.class);
		assertArrayEquals(new String[]{"/config"}, configWithMode.getAnnotation(GetMapping.class).value());

		Method ingesterShutdownPost =
				LokiFeignClient.class.getMethod("ingesterShutdownPost", LokiIngesterShutdownRequest.class);
		PostMapping ingesterShutdownPostMapping = ingesterShutdownPost.getAnnotation(PostMapping.class);
		assertArrayEquals(new String[]{"/ingester/shutdown"}, ingesterShutdownPostMapping.value());
		assertArrayEquals(new String[]{MediaType.APPLICATION_FORM_URLENCODED_VALUE}, ingesterShutdownPostMapping.consumes());

		Method deletePut = LokiFeignClient.class.getMethod("deletePut", LokiDeleteRequest.class);
		assertArrayEquals(
				new String[]{LokiFeignClient.API_V1_PATH + "/delete"},
				deletePut.getAnnotation(PutMapping.class).value()
		);

		Method deleteGet = LokiFeignClient.class.getMethod("deleteGet");
		assertArrayEquals(
				new String[]{LokiFeignClient.API_V1_PATH + "/delete"},
				deleteGet.getAnnotation(GetMapping.class).value()
		);

		Method deleteCancel = LokiFeignClient.class.getMethod("deleteCancel", LokiDeleteCancelRequest.class);
		assertArrayEquals(
				new String[]{LokiFeignClient.API_V1_PATH + "/delete"},
				deleteCancel.getAnnotation(DeleteMapping.class).value()
		);
	}
}
