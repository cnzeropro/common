package org.zero.common.core.util.javax.web;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author zero
 * @since 2022/7/19
 */
@UtilityClass
public class ResponseUtil {

    public static void writeErrorJson(HttpServletResponse response, String jsonStr) {
        writeJson(response, jsonStr, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void writeOkJson(HttpServletResponse response, String jsonStr) {
        writeJson(response, jsonStr, HttpStatus.OK);
    }

    public static void writeJson(HttpServletResponse response, String jsonStr, HttpStatus httpStatus) {
        write(response, jsonStr, httpStatus, MediaType.APPLICATION_JSON);
    }

    public static void writeJson(ServletResponse response, String jsonStr) {
        write(response, jsonStr, MediaType.APPLICATION_JSON);
    }

    @SneakyThrows
    public static void write(HttpServletResponse response, Object obj, HttpStatus httpStatus, MediaType mediaType) {
        response.setStatus(httpStatus.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(mediaType.toString());
        PrintWriter writer = response.getWriter();
        writer.print(obj);
        writer.flush();
        writer.close();
    }

    @SneakyThrows
    public static void write(ServletResponse response, Object obj, MediaType mediaType) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(mediaType.toString());
        PrintWriter writer = response.getWriter();
        writer.print(obj);
        writer.flush();
        writer.close();
    }
}
