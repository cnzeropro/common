package org.zero.common.core.extension.feign;

import feign.HeaderMap;
import feign.Headers;
import feign.MethodMetadata;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link CustomSpringMvcContract} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class CustomSpringMvcContractTest {

	@Test
	void shouldNotWarnForGetMappingMethodAnnotation() {
		MethodMetadata metadata = this.parseSingleMetadata(SpringGetClient.class);

		assertEquals("GET", metadata.template().method());
		this.assertNoUnsupportedWarning(metadata, "GetMapping");
	}

	@Test
	void shouldNotWarnForRequestMappingMethodAnnotation() {
		MethodMetadata metadata = this.parseSingleMetadata(SpringRequestMappingClient.class);

		assertEquals("GET", metadata.template().method());
		this.assertNoUnsupportedWarning(metadata, "RequestMapping");
	}

	@Test
	void shouldNotWarnForSpringParameterAnnotations() {
		MethodMetadata metadata = this.parseSingleMetadata(SpringParameterClient.class);

		assertEquals("id", metadata.indexToName().get(0).iterator().next());
		assertEquals("name", metadata.indexToName().get(1).iterator().next());
		assertEquals("X-Trace-Id", metadata.indexToName().get(2).iterator().next());
		this.assertNoUnsupportedWarning(metadata, "PathVariable", "RequestParam", "RequestHeader");
	}

	@Test
	void shouldKeepFeignNativeAnnotationSupport() {
		MethodMetadata metadata = this.parseSingleMetadata(FeignClient.class);

		assertEquals("GET", metadata.template().method());
		assertEquals(Integer.valueOf(1), metadata.headerMapIndex());
		assertEquals(Integer.valueOf(2), metadata.queryMapIndex());
		assertEquals("id", metadata.indexToName().get(0).iterator().next());
		this.assertNoUnsupportedWarning(metadata, "RequestLine", "Headers", "Param", "HeaderMap", "QueryMap");
	}

	@Test
	void shouldNotWarnWhenSpringAndFeignMethodAnnotationsAreMixed() {
		MethodMetadata metadata = this.parseSingleMetadata(MixedMethodAnnotationClient.class);

		assertEquals("GET", metadata.template().method());
		assertTrue(metadata.template().headers().containsKey("X-Method"));
		this.assertNoUnsupportedWarning(metadata, "GetMapping", "Headers");
	}

	@Test
	void shouldWarnForUnsupportedCustomAnnotations() {
		MethodMetadata metadata = this.parseSingleMetadata(UnsupportedAnnotationClient.class);
		String warnings = metadata.warnings();

		assertTrue(warnings.contains("UnsupportedTypeAnnotation"), warnings);
		assertTrue(warnings.contains("UnsupportedMethodAnnotation"), warnings);
		assertTrue(warnings.contains("UnsupportedParameterAnnotation"), warnings);
		assertTrue(warnings.contains("not used by contract"), warnings);
	}

	@Test
	void shouldNotWarnForCollectionFormatOnClass() {
		MethodMetadata metadata = this.parseSingleMetadata(CollectionFormatClient.class);

		assertEquals(feign.CollectionFormat.CSV, metadata.template().collectionFormat());
		this.assertNoUnsupportedWarning(metadata, "CollectionFormat");
	}

	@Test
	void shouldRejectRequestMappingOnClass() {
		CustomSpringMvcContract contract = this.newContract();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> contract.parseAndValidateMetadata(IllegalClassRequestMappingClient.class));

		assertTrue(exception.getMessage().contains("@RequestMapping annotation not allowed"), exception.getMessage());
	}

	private MethodMetadata parseSingleMetadata(Class<?> type) {
		List<MethodMetadata> metadataList = this.newContract().parseAndValidateMetadata(type);
		assertEquals(1, metadataList.size());
		return metadataList.get(0);
	}

	private CustomSpringMvcContract newContract() {
		CustomSpringMvcContract contract = new CustomSpringMvcContract();
		contract.setEnvironment(new StandardEnvironment());
		contract.afterPropertiesSet();
		return contract;
	}

	private void assertNoUnsupportedWarning(MethodMetadata metadata, String... annotationNames) {
		String warnings = metadata.warnings();
		assertFalse(warnings.contains("not used by contract"), warnings);
		for (String annotationName : annotationNames) {
			assertFalse(warnings.contains(annotationName), warnings);
		}
	}

	@org.springframework.cloud.openfeign.CollectionFormat(feign.CollectionFormat.CSV)
	interface SpringGetClient {
		@GetMapping("/spring-get")
		String get();
	}

	@org.springframework.cloud.openfeign.CollectionFormat(feign.CollectionFormat.CSV)
	interface SpringRequestMappingClient {
		@RequestMapping(method = RequestMethod.GET, value = "/spring-request")
		String get();
	}

	@org.springframework.cloud.openfeign.CollectionFormat(feign.CollectionFormat.CSV)
	interface SpringParameterClient {
		@GetMapping("/spring/{id}")
		String get(@PathVariable("id") String id,
		           @RequestParam("name") String name,
		           @RequestHeader("X-Trace-Id") String traceId);
	}

	@Headers("X-Client: feign")
	interface FeignClient {
		@RequestLine("GET /feign/{id}")
		String get(@Param("id") String id,
		           @HeaderMap Map<String, Object> headers,
		           @QueryMap(encoded = true) Map<String, Object> queryMap);
	}

	@Headers("X-Client: mixed")
	interface MixedMethodAnnotationClient {
		@GetMapping("/mixed/{id}")
		@Headers("X-Method: mixed")
		String get(@PathVariable("id") String id);
	}

	@org.springframework.cloud.openfeign.CollectionFormat(feign.CollectionFormat.CSV)
	@UnsupportedTypeAnnotation
	interface UnsupportedAnnotationClient {
		@GetMapping("/unsupported")
		@UnsupportedMethodAnnotation
		String get(@UnsupportedParameterAnnotation String rawBody);
	}

	@org.springframework.cloud.openfeign.CollectionFormat(feign.CollectionFormat.CSV)
	interface CollectionFormatClient {
		@GetMapping("/collection-format")
		String get();
	}

	@RequestMapping("/illegal")
	interface IllegalClassRequestMappingClient {
		@GetMapping("/mapping")
		String get();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface UnsupportedTypeAnnotation {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface UnsupportedMethodAnnotation {
	}

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface UnsupportedParameterAnnotation {
	}
}
