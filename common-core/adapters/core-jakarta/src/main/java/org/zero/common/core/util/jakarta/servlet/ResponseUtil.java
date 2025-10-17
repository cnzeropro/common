package org.zero.common.core.util.jakarta.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;

/**
 * @author zero
 * @see org.zero.common.core.util.javax.servlet.ResponseUtil
 * @since 2022/7/19
 */
@Slf4j
@UtilityClass
public class ResponseUtil {
	public static final String TEXT_PLAIN = "text/plain";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String TEXT_HTML = "text/html";

	/* *********************************************** HttpServletResponse *********************************************** */

	public static void writeErrorJson(HttpServletResponse response, String jsonStr) {
		writeJson(response, jsonStr, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	public static void writeOkJson(HttpServletResponse response, String jsonStr) {
		writeJson(response, jsonStr, HttpServletResponse.SC_OK);
	}

	public static void writeJson(HttpServletResponse response, String jsonStr, int httpStatus) {
		write(response, jsonStr, httpStatus, APPLICATION_JSON);
	}

	public static void writeText(HttpServletResponse response, String str, int httpStatus) {
		write(response, str, httpStatus, TEXT_PLAIN);
	}

	public static void writeXml(HttpServletResponse response, String xmlStr, int httpStatus) {
		write(response, xmlStr, httpStatus, APPLICATION_XML);
	}

	public static void writeHtml(HttpServletResponse response, String htmlStr, int httpStatus) {
		write(response, htmlStr, httpStatus, TEXT_HTML);
	}

	public static void write(HttpServletResponse response, String str, int httpStatus, String contentType) {
		response.setStatus(httpStatus);
		write(response, str, contentType);
	}

	public static void write(HttpServletResponse response, byte[] bytes, int httpStatus, String contentType) {
		response.setStatus(httpStatus);
		write(response, bytes, contentType);
	}

	/* *********************************************** ServletResponse *********************************************** */

	public static void writeText(ServletResponse response, String str) {
		write(response, str, TEXT_PLAIN);
	}

	public static void writeJson(ServletResponse response, String jsonStr) {
		write(response, jsonStr, APPLICATION_JSON);
	}

	public static void writeHtml(ServletResponse response, String htmlStr) {
		write(response, htmlStr, TEXT_HTML);
	}

	@SneakyThrows
	public static void write(ServletResponse response, String str, String contentType) {
		response.setContentType(contentType);
		try (PrintWriter writer = response.getWriter()) {
			writer.write(str);
			writer.flush();
		}
	}

	@SneakyThrows
	public static void write(ServletResponse response, byte[] bytes, String contentType) {
		response.setContentType(contentType);
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(bytes);
			outputStream.flush();
		}
	}
}
