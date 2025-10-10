package org.zero.common.core.support.xss;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.IteratorEnumeration;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.multipart.MultipartFormData;
import cn.hutool.core.net.multipart.UploadFile;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.SneakyThrows;
import org.zero.common.core.support.xss.processor.UnitType;
import org.zero.common.core.support.xss.processor.XssProcessor;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

/**
 * XSS 处理请求包装器
 * <p>
 * 如需多次获取请求体，建议使用 {@linkplain org.springframework.web.util.ContentCachingRequestWrapper ContentCachingRequestWrapper} 包装请求
 *
 * @author zero
 * @since 2022/2/23
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    protected final XssProcessor processor;
    protected final Map<String, UnitType[]> processableUriMap;

    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProcessor processor, Map<String, UnitType[]> processableUriMap) {
        super(request);
        this.processor = processor;
        this.processableUriMap = processableUriMap;
    }

    @Override
    public String getRequestURI() {
        String requestURI = super.getRequestURI();
        if (!inTypes(UnitType.URI)) {
            return requestURI;
        }
        return this.processor.process(requestURI, UnitType.URI);
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer requestURL = super.getRequestURL();
        if (!inTypes(UnitType.URL)) {
            return requestURL;
        }
        return new StringBuffer(this.processor.process(requestURL.toString(), UnitType.URL));
    }

    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);
        if (!inTypes(UnitType.HEADER)) {
            return header;
        }
        return this.processor.process(header, UnitType.HEADER);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headers = super.getHeaders(name);
        if (!inTypes(UnitType.HEADER)) {
            return headers;
        }
        return new IteratorEnumeration<>(IterUtil.trans(IterUtil.asIterator(headers), header -> this.processor.process(header, UnitType.HEADER)));
    }

    @Override
    public String getParameter(String name) {
        String parameter = super.getParameter(name);
        if (!inTypes(UnitType.PARAM)) {
            return parameter;
        }
        return this.processor.process(parameter, UnitType.PARAM);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        if (!inTypes(UnitType.PARAM)) {
            return parameterValues;
        }
        return ArrayUtil.map(parameterValues, String.class, parameter -> this.processor.process(parameter, UnitType.PARAM));
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        if (!inTypes(UnitType.PARAM)) {
            return parameterMap;
        }
        return MapUtil.edit(parameterMap, e -> {
            String[] value = Arrays.stream(e.getValue())
                    .map(parameter -> this.processor.process(parameter, UnitType.PARAM))
                    .toArray(String[]::new);
            return MapUtil.entry(e.getKey(), value);
        });
    }

    /**
     * 获取查询字符串，即 URL 参数
     */
    @Override
    public String getQueryString() {
        String queryString = super.getQueryString();
        if (!inTypes(UnitType.QUERY_STRING)) {
            return queryString;
        }
        return this.processor.process(queryString, UnitType.QUERY_STRING);
    }

    /**
     * 获取请求体
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream inputStream = super.getInputStream();
        if (!inTypes(UnitType.BODY_BYTE)) {
            return inputStream;
        }
        String characterEncoding = super.getCharacterEncoding();
        Charset charset = CharsetUtil.defaultCharset();
        String result = this.processor.process(IoUtil.read(inputStream, charset), UnitType.BODY_BYTE);
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

    @SneakyThrows
    protected void parseMultipart(InputStream inputStream, Charset charset){
        MultipartFormData multipartFormData = new MultipartFormData();
        multipartFormData.parseRequestStream(inputStream, charset);
        Map<String, String[]> paramMap = multipartFormData.getParamMap();
        Map<String, UploadFile[]> fileMap = multipartFormData.getFileMap();
        // MultipartBody.from(, charset)
    }

    @Override
    public BufferedReader getReader() throws IOException {
        BufferedReader reader = super.getReader();
        if (!inTypes(UnitType.BODY_CHAR)) {
            return reader;
        }
        String input = IoUtil.read(reader);
        String result = this.processor.process(input, UnitType.BODY_CHAR);
        return new BufferedReader(new StringReader(result));
    }

    protected static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    protected boolean inTypes(UnitType type) {
        String requestURI = super.getRequestURI();
        for (String uri : processableUriMap.keySet()) {
            if (PATH_MATCHER.match(uri, requestURI)) {
                UnitType[] unitTypes = processableUriMap.get(uri);
                return ArrayUtil.contains(unitTypes, type);
            }
        }
        return false;
    }
}
