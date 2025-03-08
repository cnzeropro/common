package org.zero.common.core.util.javax.servlet;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
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
    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";

    /* *********************************************** HttpServletResponse *********************************************** */

    public static void writeErrorJson(HttpServletResponse response, String jsonStr) {
        writeJson(response, jsonStr, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public static void writeOkJson(HttpServletResponse response, String jsonStr) {
        writeJson(response, jsonStr, HttpServletResponse.SC_OK);
    }

    public static void writeJson(HttpServletResponse response, String jsonStr, int httpStatus) {
        writeAndClose(response, jsonStr, httpStatus, APPLICATION_JSON);
    }

    public static void writeText(HttpServletResponse response, String str, int httpStatus) {
        writeAndClose(response, str, httpStatus, TEXT_PLAIN);
    }

    public static void writeHtml(HttpServletResponse response, String htmlStr, int httpStatus) {
        writeAndClose(response, htmlStr, httpStatus, TEXT_HTML);
    }

    public static void write(HttpServletResponse response, Object obj, int httpStatus, String contentType) {
        response.setStatus(httpStatus);
        write(response, obj, contentType);
    }

    public static void writeAndClose(HttpServletResponse response, Object obj, int httpStatus, String contentType) {
        response.setStatus(httpStatus);
        writeAndClose(response, obj, contentType);
    }

    /* *********************************************** ServletResponse *********************************************** */

    public static void writeJson(ServletResponse response, String jsonStr) {
        writeAndClose(response, jsonStr, APPLICATION_JSON);
    }

    public static void writeText(ServletResponse response, String str) {
        writeAndClose(response, str, TEXT_PLAIN);
    }

    public static void writeHtml(ServletResponse response, String htmlStr) {
        writeAndClose(response, htmlStr, TEXT_HTML);
    }

    public static void write(ServletResponse response, Object obj, String contentType) {
        response.setContentType(contentType);
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

    public static void writeAndClose(ServletResponse response, Object obj, String contentType) {
        write(response, obj, contentType);
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
