package org.zero.common.test.aspectj.ctw;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/14
 */
class LoggableAspectCtwTest {
    @Test
    void shouldLogExecutionTimeForCtwScenario() throws Exception {
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalError = System.err;
        try (PrintStream redirectedError = new PrintStream(errorStream, true, StandardCharsets.UTF_8.name())) {
            System.setErr(redirectedError);
            CtwLoggableService service = new CtwLoggableService();
            assertEquals("ok:demo", service.ok("demo"));
            IllegalStateException exception = assertThrows(IllegalStateException.class, service::fail);
            assertEquals("boom", exception.getMessage());
        } finally {
            System.setErr(originalError);
        }

        String logs = errorStream.toString(StandardCharsets.UTF_8.name());
        assertTrue(logs.contains("CtwLoggableService.ok(java.lang.String)"),
                () -> "Missing CTW success log. Actual logs: " + logs);
        assertTrue(logs.contains("CtwLoggableService.fail()"),
                () -> "Missing CTW failure log. Actual logs: " + logs);
    }
}
