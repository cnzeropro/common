package org.zero.common.core.xss;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.IteratorEnumeration;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author zero
 * @since 2022/2/23
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getRequestURI() {
        String requestURI = super.getRequestURI();
        return this.escape(requestURI);
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer requestURL = super.getRequestURL();
        return new StringBuffer(this.escape(requestURL.toString()));
    }

    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);
        return this.escape(header);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headers = super.getHeaders(name);
        return new IteratorEnumeration<>(IterUtil.trans(IterUtil.asIterator(headers), this::escape));
    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        return this.escape(parameter);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        return ArrayUtil.map(parameterValues, String.class, this::escape);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        return MapUtil.edit(parameterMap, e -> new Map.Entry<String, String[]>() {
            @Override
            public String getKey() {
                return e.getKey();
            }

            @Override
            public String[] getValue() {
                return ArrayUtil.map(e.getValue(), String.class, XssHttpServletRequestWrapper.this::escape);
            }

            @Override
            public String[] setValue(String[] value) {
                throw new UnsupportedOperationException("Unsupported [setValue] method");
            }
        });
    }

    /**
     * 获取查询字符串，即 URL 参数
     */
    @Override
    public String getQueryString() {
        String queryString = super.getQueryString();
        return this.escape(queryString);
    }

    /**
     * 获取请求体
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream inputStream = super.getInputStream();
        String escaped = this.escape(IoUtil.read(inputStream, StandardCharsets.UTF_8));
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(escaped.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public int available() throws IOException {
                return byteArrayInputStream.available();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        BufferedReader reader = super.getReader();
        String input = IoUtil.read(reader);
        String escaped = this.escape(input);
        return new BufferedReader(new StringReader(escaped));
    }

    /**
     * xss 字符检查
     *
     * @return true：存在 xss 字符，反之则返
     */
    private boolean check(String input) {
        return false;
    }

    /**
     * xss 字符转义
     */
    private String escape(String input) {
        return org.apache.commons.text.StringEscapeUtils.escapeHtml4(input);
    }

    /**
     * xss 字符过滤
     */
    private String filter(String input) {
        return cn.hutool.http.HtmlUtil.filter(input);
    }
}
