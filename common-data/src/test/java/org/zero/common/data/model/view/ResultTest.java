package org.zero.common.data.model.view;

import org.junit.jupiter.api.Test;
import org.zero.common.data.enumeration.Status;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class ResultTest {

	@Test
	void okFactoriesShouldPopulateSuccessMetadata() {
		Result<Void> emptyResult = Result.ok();
		Result<Integer> dataResult = Result.ok(10);
		Result<String> messageResult = Result.ok((CharSequence) "created", "payload");

		assertAll(
			() -> assertSame(Status.Default.OK, emptyResult.getStatus()),
			() -> assertTrue(emptyResult.isSuccess()),
			() -> assertNull(emptyResult.getData()),
			() -> assertNotNull(emptyResult.getTime()),
			() -> assertSame(Status.Default.OK, dataResult.getStatus()),
			() -> assertEquals(Integer.valueOf(10), dataResult.getData()),
			() -> assertSame(Status.OK_CODE, dataResult.getCode()),
			() -> assertEquals(Status.Default.OK.getMessage(), dataResult.getMessage()),
			() -> assertSame(Status.OK_CODE, messageResult.getCode()),
			() -> assertEquals("created", messageResult.getMessage()),
			() -> assertEquals("payload", messageResult.getData())
		);
	}

	@Test
	void errorFactoriesShouldPopulateFailureMetadata() {
		Result<Void> emptyResult = Result.error();
		Result<String> customResult = Result.error(404, "not found", "payload");

		assertAll(
			() -> assertSame(Status.Default.ERROR, emptyResult.getStatus()),
			() -> assertFalse(emptyResult.isSuccess()),
			() -> assertNull(emptyResult.getData()),
			() -> assertNotNull(emptyResult.getTime()),
			() -> assertEquals(404, customResult.getCode()),
			() -> assertEquals("not found", customResult.getMessage()),
			() -> assertEquals("payload", customResult.getData()),
			() -> assertFalse(customResult.isSuccess())
		);
	}

	@Test
	void ofFactoryShouldDeriveSuccessFromStatusCode() {
		Result<String> okResult = Result.of(Status.Default.OK, "payload");
		Result<String> errorResult = Result.of(Status.Default.of("A0400", "invalid request"), "payload");

		assertAll(
			() -> assertTrue(okResult.isSuccess()),
			() -> assertFalse(errorResult.isSuccess()),
			() -> assertEquals("A0400", errorResult.getCode()),
			() -> assertEquals("invalid request", errorResult.getMessage())
		);
	}

	@Test
	void ofFactoryShouldRespectExplicitSuccessAndTime() {
		LocalDateTime time = LocalDateTime.of(2026, 4, 3, 12, 30, 45);
		Result<String> result = Result.of(Status.Default.ERROR, "payload", true, time);

		assertAll(
			() -> assertSame(Status.Default.ERROR, result.getStatus()),
			() -> assertTrue(result.isSuccess()),
			() -> assertEquals("payload", result.getData()),
			() -> assertSame(time, result.getTime())
		);
	}
}
