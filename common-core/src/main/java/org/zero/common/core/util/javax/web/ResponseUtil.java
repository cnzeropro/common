package org.zero.common.core.util.javax.web;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.zero.common.data.exception.UtilException;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zero
 * @since 2022/7/19
 */
@Slf4j
@UtilityClass
public class ResponseUtil {
    /* *********************************************** HttpServletResponse *********************************************** */

    public static void writeErrorJson(HttpServletResponse response, String jsonStr) {
        writeJson(response, jsonStr, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static void writeOkJson(HttpServletResponse response, String jsonStr) {
        writeJson(response, jsonStr, HttpStatus.OK);
    }

    public static void writeJson(HttpServletResponse response, String jsonStr, HttpStatus httpStatus) {
        writeAndClose(response, jsonStr, httpStatus, MediaType.APPLICATION_JSON);
    }

    public static void writeText(HttpServletResponse response, String str, HttpStatus httpStatus) {
        writeAndClose(response, str, httpStatus, MediaType.TEXT_PLAIN);
    }

    public static void writeHtml(HttpServletResponse response, String htmlStr, HttpStatus httpStatus) {
        writeAndClose(response, htmlStr, httpStatus, MediaType.TEXT_HTML);
    }

    public static void write(HttpServletResponse response, Object obj, HttpStatus httpStatus, MediaType mediaType) {
        response.setStatus(httpStatus.value());
        write(response, obj, mediaType);
    }

    public static void writeAndClose(HttpServletResponse response, Object obj, HttpStatus httpStatus, MediaType mediaType) {
        response.setStatus(httpStatus.value());
        writeAndClose(response, obj, mediaType);
    }

    /* *********************************************** ServletResponse *********************************************** */

    public static void writeJson(ServletResponse response, String jsonStr) {
        writeAndClose(response, jsonStr, MediaType.APPLICATION_JSON);
    }

    public static void writeText(ServletResponse response, String str) {
        writeAndClose(response, str, MediaType.TEXT_PLAIN);
    }

    public static void writeHtml(ServletResponse response, String htmlStr) {
        writeAndClose(response, htmlStr, MediaType.TEXT_HTML);
    }

    public static void write(ServletResponse response, Object obj, MediaType mediaType) {
        response.setContentType(mediaType.toString());
        String objStr = String.valueOf(obj);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.print(objStr);
            outputStream.flush();
        } catch (IllegalStateException ignored) {
            try {
                PrintWriter writer = response.getWriter();
                writer.print(objStr);
                writer.flush();
            } catch (Exception e) {
                throw new UtilException("Response writer write error", e);
            }
        } catch (IOException e) {
            throw new UtilException("Response stream write error", e);
        }
    }

    public static void writeAndClose(ServletResponse response, Object obj, MediaType mediaType) {
        write(response, obj, mediaType);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.close();
        } catch (IllegalStateException ignored) {
            try {
                PrintWriter writer = response.getWriter();
                writer.close();
            } catch (Exception e) {
                throw new UtilException("Response writer close error", e);
            }
        } catch (IOException e) {
            throw new UtilException("Response stream close error", e);
        }
    }
}
