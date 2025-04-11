package org.zero.common.core.support.xss;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.IteratorEnumeration;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import org.zero.common.core.support.xss.processor.Type;
import org.zero.common.core.support.xss.processor.XssProcessor;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author zero
 * @since 2022/2/23
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    protected final XssProcessor processor;

    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProcessor processor) {
        super(request);
        this.processor = processor;
    }

    @Override
    public String getRequestURI() {
        String requestURI = super.getRequestURI();
        return this.processor.process(requestURI, Type.URI);
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer requestURL = super.getRequestURL();
        return new StringBuffer(this.processor.process(requestURL.toString(), Type.URL));
    }

    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);
        return this.processor.process(header, Type.HEADER);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headers = super.getHeaders(name);
        return new IteratorEnumeration<>(IterUtil.trans(IterUtil.asIterator(headers), header -> this.processor.process(header, Type.HEADER)));
    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        return this.processor.process(parameter, Type.PARAM);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        return ArrayUtil.map(parameterValues, String.class, parameter -> this.processor.process(parameter, Type.PARAM));
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
                return ArrayUtil.map(e.getValue(), String.class, parameter -> XssHttpServletRequestWrapper.this.processor.process(parameter, Type.PARAM));
            }

            @Override
            public String[] setValue(String[] value) {
                throw new UnsupportedOperationException();
            }
        });
    }

    /**
     * 获取查询字符串，即 URL 参数
     */
    @Override
    public String getQueryString() {
        String queryString = super.getQueryString();
        return this.processor.process(queryString, Type.QUERY_STRING);
    }

    /**
     * 获取请求体
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        String characterEncoding = super.getCharacterEncoding();
        ServletInputStream inputStream = super.getInputStream();
        Charset charset = CharsetUtil.charset(characterEncoding);
        String result = this.processor.process(IoUtil.read(inputStream, charset), Type.BODY_INPUT_STREAM);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(result.getBytes(characterEncoding));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException();
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
        String result = this.processor.process(input, Type.BODY_READER);
        return new BufferedReader(new StringReader(result));
    }
}
