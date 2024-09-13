package org.zero.common.core.extension.export.archive;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.compress.Deflate;
import cn.hutool.core.compress.Gzip;
import cn.hutool.core.compress.ZipWriter;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
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
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.extension.export.BaseExportResponseBodyAdvice;
import org.zero.common.core.extension.export.FileExportEntity;
import org.zero.common.data.exception.CommonException;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.zip.ZipOutputStream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/11
 */
@RestControllerAdvice
public class ArchiveExportResponseBodyAdvice extends BaseExportResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(ArchiveExport.class);
    }

    @Override
    protected FileExportEntity export(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        ArchiveExport archiveExport = Optional.ofNullable(returnType.getMethodAnnotation(ArchiveExport.class))
                // never goto here
                .orElseThrow(() -> new CommonException("@ArchiveExport annotation is null"));
        if (body instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) body;
            return this.handleCharSequence(archiveExport, charSequence);
        }
        if (body instanceof URI) {
            URI uri = (URI) body;
            return this.handleUri(archiveExport, uri);
        }
        if (body instanceof URL) {
            URL url = (URL) body;
            return this.handleUrl(archiveExport, url);
        }
        if (body instanceof Path) {
            Path path = (Path) body;
            return this.handlePath(archiveExport, path);
        }
        if (body instanceof File) {
            File file = (File) body;
            return this.handleFile(archiveExport, file);
        }
        if (body instanceof Reader) {
            Reader reader = (Reader) body;
            return this.handleReader(archiveExport, reader);
        }
        if (body instanceof InputStream) {
            InputStream inputStream = (InputStream) body;
            return this.handleStream(archiveExport, inputStream);
        }
        if (body instanceof Resource) {
            Resource resource = (Resource) body;
            return this.handleResource(archiveExport, resource);
        }
        if (ArrayUtil.isArray(body)) {
            Object[] objects = (Object[]) body;
            return this.handleArray(archiveExport, objects);
        }
        if (body instanceof Collection) {
            Collection<?> collection = (Collection<?>) body;
            return this.handleCollection(archiveExport, collection);
        }
        if (body instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) body;
            return this.handleMap(archiveExport, map);
        }
        throw new CommonException(String.format("@ArchiveExport annotation method does not support the return type: %s", body.getClass()));
    }

    protected FileExportEntity handleArray(ArchiveExport archiveExport, Object[] objects) {
        if (allNonNullMatch(objects, CharSequence.class::isInstance)) {
            CharSequence[] charSequences = (CharSequence[]) objects;
            return this.handleCharSequence(archiveExport, charSequences);
        }
        if (allNonNullMatch(objects, URI.class::isInstance)) {
            URI[] uris = (URI[]) objects;
            return this.handleUri(archiveExport, uris);
        }
        if (allNonNullMatch(objects, URL.class::isInstance)) {
            URL[] urls = (URL[]) objects;
            return this.handleUrl(archiveExport, urls);
        }
        if (allNonNullMatch(objects, Path.class::isInstance)) {
            Path[] paths = (Path[]) objects;
            return this.handlePath(archiveExport, paths);
        }
        if (allNonNullMatch(objects, File.class::isInstance)) {
            File[] files = (File[]) objects;
            return this.handleFile(archiveExport, files);
        }
        if (allNonNullMatch(objects, Reader.class::isInstance)) {
            Reader[] readers = (Reader[]) objects;
            return this.handleReader(archiveExport, readers);
        }
        if (allNonNullMatch(objects, InputStream.class::isInstance)) {
            InputStream[] inputStreams = (InputStream[]) objects;
            return this.handleStream(archiveExport, inputStreams);
        }
        if (allNonNullMatch(objects, Resource.class::isInstance)) {
            Resource[] resources = (Resource[]) objects;
            return this.handleResource(archiveExport, resources);
        }
        throw new CommonException(String.format("@ArchiveExport annotation method does not support the return type: %s[]", ArrayUtil.getComponentType(objects)));
    }

    protected FileExportEntity handleCollection(ArchiveExport archiveExport, Collection<?> collection) {
        if (allNonNullMatch(collection, CharSequence.class::isInstance)) {
            CharSequence[] charSequences = collection.toArray(new CharSequence[0]);
            return this.handleCharSequence(archiveExport, charSequences);
        }
        if (allNonNullMatch(collection, URI.class::isInstance)) {
            URI[] uris = collection.toArray(new URI[0]);
            return this.handleUri(archiveExport, uris);
        }
        if (allNonNullMatch(collection, URL.class::isInstance)) {
            URL[] urls = collection.toArray(new URL[0]);
            return this.handleUrl(archiveExport, urls);
        }
        if (allNonNullMatch(collection, Path.class::isInstance)) {
            Path[] paths = collection.toArray(new Path[0]);
            return this.handlePath(archiveExport, paths);
        }
        if (allNonNullMatch(collection, File.class::isInstance)) {
            File[] files = collection.toArray(new File[0]);
            return this.handleFile(archiveExport, files);
        }
        if (allNonNullMatch(collection, Reader.class::isInstance)) {
            Reader[] readers = collection.toArray(new Reader[0]);
            return this.handleReader(archiveExport, readers);
        }
        if (allNonNullMatch(collection, InputStream.class::isInstance)) {
            InputStream[] inputStreams = collection.toArray(new InputStream[0]);
            return this.handleStream(archiveExport, inputStreams);
        }
        if (allNonNullMatch(collection, Resource.class::isInstance)) {
            Resource[] resources = collection.toArray(new Resource[0]);
            return this.handleResource(archiveExport, resources);
        }
        throw new CommonException(String.format("@ArchiveExport annotation method does not support the return type: %s<%s>", collection.getClass(), IterUtil.getElementType(collection)));
    }

    protected FileExportEntity handleMap(ArchiveExport archiveExport, Map<?, ?> map) {
        Collection<?> values = map.values();
        if (allNonNullMatch(values, CharSequence.class::isInstance)) {
            Map<?, CharSequence> charSequenceMap = mapMapValue(map, CharSequence.class::cast);
            return this.handleCharSequence(archiveExport, charSequenceMap);
        }
        if (allNonNullMatch(values, URI.class::isInstance)) {
            Map<?, URI> uriMap = mapMapValue(map, URI.class::cast);
            return this.handleUri(archiveExport, uriMap);
        }
        if (allNonNullMatch(values, URL.class::isInstance)) {
            Map<?, URL> urlMap = mapMapValue(map, URL.class::cast);
            return this.handleUrl(archiveExport, urlMap);
        }
        if (allNonNullMatch(values, Path.class::isInstance)) {
            Map<?, Path> pathMap = mapMapValue(map, Path.class::cast);
            return this.handlePath(archiveExport, pathMap);
        }
        if (allNonNullMatch(values, File.class::isInstance)) {
            Map<?, File> fileMap = mapMapValue(map, File.class::cast);
            return this.handleFile(archiveExport, fileMap);
        }
        if (allNonNullMatch(values, Reader.class::isInstance)) {
            Map<?, Reader> readerMap = mapMapValue(map, Reader.class::cast);
            return this.handleReader(archiveExport, readerMap);
        }
        if (allNonNullMatch(values, InputStream.class::isInstance)) {
            Map<?, InputStream> inputStreamMap = mapMapValue(map, InputStream.class::cast);
            return this.handleStream(archiveExport, inputStreamMap);
        }
        if (allNonNullMatch(values, Resource.class::isInstance)) {
            Map<?, Resource> resourceMap = mapMapValue(map, Resource.class::cast);
            return this.handleResource(archiveExport, resourceMap);
        }
        Optional<? extends Map.Entry<?, ?>> entryOpt = map.entrySet()
                .stream()
                .filter(entry -> Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue()))
                .findFirst();
        String keyTypeName = entryOpt.map(Map.Entry::getKey)
                .map(Object::getClass)
                .map(Class::getName)
                .orElse("unknown");
        String valueTypeName = entryOpt.map(Map.Entry::getValue)
                .map(Object::getClass)
                .map(Class::getName)
                .orElse("unknown");
        throw new CommonException(String.format("@ArchiveExport annotation method does not support the return type: %s<%s, %s>", map.getClass(), keyTypeName, valueTypeName));
    }

    protected FileExportEntity handleCharSequence(ArchiveExport archiveExport, Map<?, CharSequence> charSequenceMap) {
        Map<?, URI> uriMap;
        try {
            uriMap = mapMapValue(charSequenceMap, this::getUriFromCharSequence);
        } catch (Exception ignored) {
            Map<?, File> fileMap = mapMapValue(charSequenceMap, this::getFileFromCharSequence);
            return this.handleFile(archiveExport, fileMap);
        }
        return this.handleUri(archiveExport, uriMap);
    }

    protected FileExportEntity handleCharSequence(ArchiveExport archiveExport, CharSequence... charSequences) {
        URI[] uris;
        try {
            uris = mapArray(charSequences, this::getUriFromCharSequence);
        } catch (Exception ignored) {
            File[] files = mapArray(charSequences, this::getFileFromCharSequence);
            return this.handleFile(archiveExport, files);
        }
        return this.handleUri(archiveExport, uris);
    }

    private URI getUriFromCharSequence(CharSequence charSequence) {
        return URI.create(charSequence.toString());
    }

    @SneakyThrows
    private File getFileFromCharSequence(CharSequence charSequence) {
        return ResourceUtils.getFile(charSequence.toString());
    }

    protected FileExportEntity handleUri(ArchiveExport archiveExport, Map<?, URI> uriMap) {
        Map<?, URL> urlMap;
        try {
            urlMap = mapMapValue(uriMap, this::getUrlFromUri);
        } catch (Exception ignored) {
            Map<?, File> fileMap = mapMapValue(uriMap, this::getFileFromUri);
            return this.handleFile(archiveExport, fileMap);
        }
        return this.handleUrl(archiveExport, urlMap);
    }

    protected FileExportEntity handleUri(ArchiveExport archiveExport, URI... uris) {
        URL[] urls;
        try {
            urls = mapArray(uris, this::getUrlFromUri);
        } catch (Exception ignored) {
            File[] files = mapArray(uris, this::getFileFromUri);
            return this.handleFile(archiveExport, files);
        }
        return this.handleUrl(archiveExport, urls);
    }

    @SneakyThrows
    private URL getUrlFromUri(URI uri) {
        return uri.toURL();
    }

    @SneakyThrows
    private File getFileFromUri(URI uri) {
        return ResourceUtils.getFile(uri);
    }

    protected FileExportEntity handleUrl(ArchiveExport archiveExport, Map<?, URL> urlMap) {
        if (allNonNullMatch(urlMap.values(), ResourceUtils::isFileURL)) {
            Map<?, File> fileMap = mapMapValue(urlMap, this::getFileFromUrl);
            return this.handleFile(archiveExport, fileMap);
        } else {
            Map<?, InputStream> inputStreamMap = mapMapValue(urlMap, URLUtil::getStream);
            return this.handleStream(archiveExport, inputStreamMap);
        }
    }

    protected FileExportEntity handleUrl(ArchiveExport archiveExport, URL... urls) {
        if (allNonNullMatch(urls, ResourceUtils::isFileURL)) {
            File[] files = mapArray(urls, this::getFileFromUrl);
            return this.handleFile(archiveExport, files);
        } else {
            InputStream[] inputStreams = mapArray(urls, URLUtil::getStream);
            return this.handleStream(archiveExport, inputStreams);
        }
    }

    @SneakyThrows
    private File getFileFromUrl(URL url) {
        return ResourceUtils.getFile(url);
    }

    protected FileExportEntity handlePath(ArchiveExport archiveExport, Map<?, Path> pathMap) {
        Map<?, File> fileMap = mapMapValue(pathMap, Path::toFile);
        return this.handleFile(archiveExport, fileMap);
    }

    protected FileExportEntity handlePath(ArchiveExport archiveExport, Path... paths) {
        File[] files = mapArray(paths, Path::toFile);
        return this.handleFile(archiveExport, files);
    }

    protected FileExportEntity handleResource(ArchiveExport archiveExport, Map<?, Resource> resourceMap) {
        Map<?, File> fileMap;
        try {
            fileMap = mapMapValue(resourceMap, this::getFileFromResource);
        } catch (Exception e) {
            Map<?, InputStream> inputStreamMap = mapMapValue(resourceMap, this::getInputStreamFromResource);
            return this.handleStream(archiveExport, inputStreamMap);
        }
        return this.handleFile(archiveExport, fileMap);
    }

    protected FileExportEntity handleResource(ArchiveExport archiveExport, Resource... resources) {
        File[] files;
        try {
            files = mapArray(resources, this::getFileFromResource);
        } catch (Exception ignored) {
            int i = 0;
            Map<String, InputStream> inputStreamMap = new LinkedHashMap<>();
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (!StringUtils.hasText(filename)) {
                    filename = String.format("file_%d", i++);
                }
                inputStreamMap.put(filename, this.getInputStreamFromResource(resource));
            }
            return this.handleStream(archiveExport, inputStreamMap);
        }
        return this.handleFile(archiveExport, files);
    }

    @SneakyThrows
    private File getFileFromResource(Resource resource) {
        return resource.getFile();
    }

    @SneakyThrows
    private InputStream getInputStreamFromResource(Resource resource) {
        return resource.getInputStream();
    }

    protected FileExportEntity handleFile(ArchiveExport archiveExport, Map<?, File> fileMap) {
        Map<?, InputStream> inputStreamMap = mapMapValue(fileMap, FileUtil::getInputStream);
        return this.handleStream(archiveExport, inputStreamMap);
    }

    protected FileExportEntity handleFile(ArchiveExport archiveExport, File... files) {
        ArchiveFileType archiveFileType = archiveExport.fileType();
        if (archiveFileType == ArchiveFileType.ZIP) {
            FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
            @Cleanup ZipWriter zipWriter = this.getZipWriter(archiveExport, outputStream);
            zipWriter.add(archiveExport.withSrcDir(), pathname -> true, files);
            return FileExportEntity.of(archiveExport.filename(), outputStream, archiveFileType.getExtName(), archiveFileType.getContentType());
        } else {
            if (files.length > 1) {
                throw new CommonException("Only ZIP support multi file");
            }
            File file = files[0];
            InputStream inputStream = FileUtil.getInputStream(file);
            return this.handleStream(archiveExport, inputStream);
        }
    }

    protected FileExportEntity handleReader(ArchiveExport archiveExport, Map<?, Reader> readerMap) {
        Map<?, InputStream> inputStreamMap = mapMapValue(readerMap, reader -> this.getInputStreamFromReader(archiveExport, reader));
        return this.handleStream(archiveExport, inputStreamMap);
    }

    protected FileExportEntity handleReader(ArchiveExport archiveExport, Reader... readers) {
        InputStream[] inputStreams = mapArray(readers, reader -> this.getInputStreamFromReader(archiveExport, reader));
        return this.handleStream(archiveExport, inputStreams);
    }

    private InputStream getInputStreamFromReader(ArchiveExport archiveExport, Reader reader) {
        @Cleanup FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        // 尝试获取字符集
        Charset charset;
        if (reader instanceof InputStreamReader) {
            InputStreamReader inputStreamReader = (InputStreamReader) reader;
            charset = Charset.forName(inputStreamReader.getEncoding());
        } else {
            try {
                charset = Charset.forName(archiveExport.charset());
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

    protected FileExportEntity handleStream(ArchiveExport archiveExport, Map<?, InputStream> inputStreamMap) {
        ArchiveFileType archiveFileType = archiveExport.fileType();
        FastByteArrayOutputStream outputStream;
        if (archiveFileType == ArchiveFileType.ZIP) {
            outputStream = this.handleStreamByZip(archiveExport, inputStreamMap);
        } else if (archiveFileType == ArchiveFileType.GZIP || archiveFileType == ArchiveFileType.ZLIB) {
            if (inputStreamMap.size() > 1) {
                throw new CommonException("Only support one stream when using GZIP or ZLIB");
            }
            InputStream inputStream = inputStreamMap.values()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new CommonException("InputStream is empty"));
            if (archiveFileType == ArchiveFileType.GZIP) {
                outputStream = this.handleStreamByGzip(archiveExport, inputStream);
            } else {
                outputStream = this.handleStreamByZlib(archiveExport, inputStream);
            }
        } else {
            throw new CommonException(String.format("Unsupported compress file type: %s", archiveFileType));
        }

        return FileExportEntity.of(archiveExport.filename(), outputStream, archiveFileType.getExtName(), archiveFileType.getContentType());
    }

    protected FileExportEntity handleStream(ArchiveExport archiveExport, InputStream... inputStreams) {
        int i = 0;
        Map<String, InputStream> inputStreamMap = new LinkedHashMap<>();
        for (InputStream inputStream : inputStreams) {
            String path = String.format("file_%d", i++);
            inputStreamMap.put(path, inputStream);
        }
        return this.handleStream(archiveExport, inputStreamMap);
    }

    private FastByteArrayOutputStream handleStreamByZip(ArchiveExport archiveExport, Map<?, InputStream> inputStreamMap) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup ZipWriter zipWriter = this.getZipWriter(archiveExport, outputStream);
        String[] paths = inputStreamMap.keySet().stream().map(Objects::toString).toArray(String[]::new);
        InputStream[] inputStreams = inputStreamMap.values().toArray(new InputStream[0]);
        zipWriter.add(paths, inputStreams);
        return outputStream;
    }

    private ZipWriter getZipWriter(ArchiveExport archiveExport, OutputStream outputStream) {
        Charset charset;
        try {
            charset = Charset.forName(archiveExport.charset());
        } catch (Exception ignored) {
            charset = StandardCharsets.UTF_8;
        }
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, charset);
        zipOutputStream.setMethod(archiveExport.method());
        ZipWriter zipWriter = new ZipWriter(zipOutputStream);
        zipWriter.setComment(archiveExport.comment());
        zipWriter.setLevel(archiveExport.level());
        return zipWriter;
    }

    private FastByteArrayOutputStream handleStreamByGzip(ArchiveExport archiveExport, InputStream inputStream) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Gzip gzip = Gzip.of(inputStream, outputStream);
        gzip.gzip();
        return outputStream;
    }

    private FastByteArrayOutputStream handleStreamByZlib(ArchiveExport archiveExport, InputStream inputStream) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        @Cleanup Deflate deflate = Deflate.of(inputStream, outputStream, false);
        deflate.deflater(archiveExport.level());
        return outputStream;
    }

    protected static <K, V, R> Map<K, R> mapMapValue(Map<K, V> map, Function<V, R> function) {
        return map.entrySet().stream().collect(CollectorUtil.toMap(Map.Entry::getKey,
                entry -> function.apply(entry.getValue()),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new));
    }

    @SuppressWarnings("unchecked")
    protected static <T, R> R[] mapArray(T[] array, Function<T, R> function) {
        return Arrays.stream(array).map(function).toArray(size -> (R[]) new Object[size]);
    }
}
