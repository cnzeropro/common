package org.zero.common.test.aspectj.ltw;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
class LoggableAspectLtwTest {
    @Test
    void shouldLogExecutionTimeForLtwScenario() throws Exception {
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalError = System.err;
        try (PrintStream redirectedError = new PrintStream(errorStream, true, StandardCharsets.UTF_8.name())) {
            System.setErr(redirectedError);
            try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LtwDemoConfiguration.class)) {
                LtwLoggableService service = context.getBean(LtwLoggableService.class);
                assertEquals("ok:demo", service.ok("demo"));
                IllegalStateException exception = assertThrows(IllegalStateException.class, service::fail);
                assertEquals("boom", exception.getMessage());
            }
        } finally {
            System.setErr(originalError);
        }

        String logs = errorStream.toString(StandardCharsets.UTF_8.name());
        assertTrue(logs.contains("LtwLoggableService.ok(java.lang.String)"),
                () -> "Missing LTW success log. Actual logs: " + logs);
        assertTrue(logs.contains("LtwLoggableService.fail()"),
                () -> "Missing LTW failure log. Actual logs: " + logs);
    }
}
