package org.zero.common.core.support.export;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zero.common.data.exception.CommonException;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/10
 */
@ControllerAdvice
public class FileExportResponseBodyAdvice extends BaseExportResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(FileExport.class);
    }

    @Override
    protected FileExportEntity export(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        FileExport fileExport = Optional.ofNullable(returnType.getMethodAnnotation(FileExport.class))
                // never goto here
                .orElseThrow(() -> new CommonException("@FileExport annotation is null"));
        if (body instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) body;
            return this.handleCharSequence(fileExport, charSequence);
        }
        if (body instanceof URI) {
            URI uri = (URI) body;
            return this.handleUri(fileExport, uri);
        }
        if (body instanceof URL) {
            URL url = (URL) body;
            return this.handleUrl(fileExport, url);
        }
        if (body instanceof Resource) {
            Resource resource = (Resource) body;
            return this.handleResource(fileExport, resource);
        }
        if (body instanceof File) {
            File file = (File) body;
            return this.handleFile(fileExport, file);
        }
        if (body instanceof Reader) {
            Reader reader = (Reader) body;
            return this.handleReader(fileExport, reader);
        }
        if (body instanceof InputStream) {
            InputStream inputStream = (InputStream) body;
            return this.handleStream(fileExport, inputStream);
        }
        throw new CommonException(String.format("@FileExport annotation method does not support the return type: %s", body.getClass()));
    }

    protected FileExportEntity handleCharSequence(FileExport fileExport, CharSequence charSequence) {
        return this.handleString(fileExport, charSequence.toString());
    }

    @SneakyThrows
    private FileExportEntity handleString(FileExport fileExport, String string) {
        URI uri;
        try {
            uri = URI.create(string);
        } catch (Exception ignored) {
            File file = ResourceUtils.getFile(string);
            return this.handleFile(fileExport, file);
        }
        return this.handleUri(fileExport, uri);
    }

    @SneakyThrows
    protected FileExportEntity handleUri(FileExport fileExport, URI uri) {
        URL url;
        try {
            url = uri.toURL();
        } catch (Exception ignored) {
            // 构建 URL 报错尝试构建 File
            File file = ResourceUtils.getFile(uri);
            return this.handleFile(fileExport, file);
        }
        return this.handleUrl(fileExport, url);
    }

    @SneakyThrows
    protected FileExportEntity handleUrl(FileExport fileExport, URL url) {
        if (ResourceUtils.isFileURL(url)) {
            File file = ResourceUtils.getFile(url);
            return this.handleFile(fileExport, file);
        }
        // 非 file 协议，尝试直接获取流
        InputStream inputStream = url.openStream();
        return this.handleStream(fileExport, inputStream);
    }

    @SneakyThrows
    protected FileExportEntity handleResource(FileExport fileExport, Resource resource) {
        File file;
        try {
            file = resource.getFile();
        } catch (Exception ignored) {
            String filename = resource.getFilename();
            InputStream inputStream = resource.getInputStream();
            return this.handleStream(fileExport, filename, inputStream);
        }
        return this.handleFile(fileExport, file);
    }

    protected FileExportEntity handleFile(FileExport fileExport, File file) {
        if (file.exists()) {
            if (file.isFile()) {
                String filename = FileUtil.getName(file);
                InputStream inputStream = FileUtil.getInputStream(file);
                return this.handleStream(fileExport, filename, inputStream);
            }
            throw new CommonException(String.format("The value is not a supported file: %s", file));
        }
        throw new CommonException(String.format("The value is not an existing file: %s", file));
    }

    /**
     * 处理字符流
     * <p>
     * 注意：因为原始 Reader 对象本身并不直接携带字符集信息，所以有可能乱码，建议少用
     */
    protected FileExportEntity handleReader(FileExport fileExport, Reader reader) {
        InputStream inputStream = this.getInputStreamFromReader(fileExport, reader);
        return this.handleStream(fileExport, inputStream);
    }

    private InputStream getInputStreamFromReader(FileExport fileExport, Reader reader) {
        @Cleanup FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        // 尝试获取字符集
        Charset charset;
        if (reader instanceof InputStreamReader) {
            InputStreamReader inputStreamReader = (InputStreamReader) reader;
            charset = Charset.forName(inputStreamReader.getEncoding());
        } else {
            try {
                charset = Charset.forName(fileExport.charset());
            } catch (Exception ignored) {
                charset = Charset.defaultCharset();
            }
        }
        OutputStreamWriter writer = new OutputStreamWriter(out, charset);
        IoUtil.copy(reader, writer);
        IoUtil.close(reader);
        IoUtil.flush(writer);
        IoUtil.close(writer);
        return out.getInputStream();
    }

    /**
     * 处理字节流
     */
    protected FileExportEntity handleStream(FileExport fileExport, InputStream inputStream) {
        return this.handleStream(fileExport, fileExport.filename(), inputStream);
    }

    private FileExportEntity handleStream(FileExport fileExport, String suggestedFileName, InputStream inputStream) {
        FastByteArrayOutputStream outputStream = this.getOutputStreamFromInputStream(inputStream);
        String filename = fileExport.filename();
        if (!StringUtils.hasText(filename)) {
            filename = suggestedFileName;
        }
        // 不能直接使用入参的 InputStream
        String extName = FileTypeUtil.getType(outputStream.getInputStream(), filename);
        // 未指定 contentType 时，尝试获取文件类型
        String contentType = fileExport.contentType();
        if (!StringUtils.hasText(contentType)) {
            // 以文件后缀名构建文件名，用其获取尝试 contentType
            String tempFileName = String.format("%s.%s", "unknown", extName);
            contentType = FileUtil.getMimeType(tempFileName);
        }
        // 没有时使用默认值：application/octet-stream
        if (!StringUtils.hasText(contentType)) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return FileExportEntity.of(filename, outputStream, extName, contentType);
    }

    private FastByteArrayOutputStream getOutputStreamFromInputStream(InputStream inputStream) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        IoUtil.copy(inputStream, outputStream);
        IoUtil.close(inputStream);
        return outputStream;
    }
}
