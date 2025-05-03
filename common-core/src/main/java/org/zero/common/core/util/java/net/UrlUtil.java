package org.zero.common.core.util.java.net;

import lombok.extern.java.Log;
import org.zero.common.core.extension.java.net.Schemes;
import org.zero.common.core.util.java.io.FileUtil;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.IfUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.core.util.java.reflect.ClassLoaderUtil;
import org.zero.common.core.util.java.util.SetUtil;
import org.zero.common.core.util.java.util.stream.StreamUtil;
import org.zero.common.data.constant.StringPool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/24
 */
@Log
public class UrlUtil {
    /**
     * ClassPath URL 前缀
     */
    public static final String URL_PREFIX_CLASSPATH = Schemes.CLASSPATH + StringPool.COLON;
    /**
     * 文件 URL 前缀
     */
    public static final String FILE_URL_PREFIX = Schemes.FILE + StringPool.COLON;
    /**
     * Jar URL 前缀
     */
    public static final String URL_PREFIX_JAR = Schemes.JAR + StringPool.COLON;
    /**
     * War URL 前缀
     */
    public static final String URL_PREFIX_WAR = Schemes.WAR + StringPool.COLON;
    /**
     * Zip URL 前缀
     */
    public static final String URL_PREFIX_ZIP = Schemes.ZIP + StringPool.COLON;
    /**
     * WebSphere URL 前缀
     */
    public static final String URL_PREFIX_WSJAR = Schemes.WSJAR + StringPool.COLON;
    /**
     * JBoss Zip URL 前缀
     */
    public static final String URL_PREFIX_VFSZIP = Schemes.VFSZIP + StringPool.COLON;
    /**
     * JBoss 文件 URL 前缀
     */
    public static final String URL_PREFIX_VFSFILE = Schemes.VFSFILE + StringPool.COLON;
    /**
     * JBoss VFS URL 前缀
     */
    public static final String URL_PREFIX_VFS = Schemes.VFS + StringPool.COLON;

    /**
     * Jar URL 的 Jar 路径以及内部文件路径的分界符: "!/"
     */
    public static final String JAR_URL_SEPARATOR = "!/";
    /**
     * War URL 的 War路径及内部文件路径分界符
     */
    public static final String WAR_URL_SEPARATOR = "*/";

    public static URL from(String url) {
        URI uri = UriUtil.from(url);
        return ObjectUtil.mapIfNonNull(uri, ThrowableUtil.sneakyThrow(URI::toURL));
    }

    public static URL from(File file) {
        URI uri = UriUtil.from(file);
        return ObjectUtil.mapIfNonNull(uri, ThrowableUtil.sneakyThrow(URI::toURL));
    }

    public static URL fromPath(String path) {
        if (StringUtil.startWith(path, UrlUtil.URL_PREFIX_CLASSPATH, true)) {
            return fromClasspath(path);
        }
        File file = FileUtil.from(path);
        return IfUtil.provide((Supplier<URL>) () -> from(file), Objects.nonNull(file));
    }

    public static URL fromClasspath(String path) {
        return fromClasspath(path, UrlUtil.class);
    }

    public static URL fromClasspath(String path, Class<?> clazz) {
        return fromClasspath(path, clazz.getClassLoader());
    }

    public static URL fromClasspath(String path, ClassLoader classLoader) {
        String normalizedPath = FileUtil.normalize(path);
        if (CharSequenceUtil.isBlank(normalizedPath)) {
            return null;
        }
        Set<ClassLoader> classLoaders = SetUtil.<ClassLoader>of(true, classLoader);
        Collections.addAll(classLoaders, ClassLoaderUtil.CLASS_LOADERS);
        for (ClassLoader cl : classLoaders) {
            if (Objects.nonNull(cl)) {
                URL url = cl.getResource(path);
                if (Objects.nonNull(url)) {
                    return url;
                }
            }
        }
        return ClassLoader.getSystemResource(path);
    }

    public static Collection<URL> listFromClasspath(String path) {
        return listFromClasspath(path, null);
    }

    public static Collection<URL> listFromClasspath(String path, ClassLoader classLoader) {
        String normalizedPath = FileUtil.normalize(path);
        if (CharSequenceUtil.isBlank(normalizedPath)) {
            return Collections.emptyList();
        }
        Set<ClassLoader> classLoaders = SetUtil.<ClassLoader>of(true, classLoader);
        Collections.addAll(classLoaders, ClassLoaderUtil.CLASS_LOADERS);
        return classLoaders.stream()
                .filter(Objects::nonNull)
                .<Enumeration<URL>>map(cl -> {
                    try {
                        return cl.getResources(path);
                    } catch (IOException e) {
                        log.log(Level.WARNING, e.getMessage(), e);
                        return Collections.emptyEnumeration();
                    }
                })
                .flatMap(StreamUtil::of)
                .distinct()
                .collect(Collectors.toList());
    }

    public static Optional<URL> getJarUrlOpt() {
        return getJarUrlOpt(UrlUtil.class);
    }

    public static Optional<URL> getJarUrlOpt(Class<?> clazz) {
        return Optional.ofNullable(clazz)
                .map(Class::getProtectionDomain)
                .map(ProtectionDomain::getCodeSource)
                .map(CodeSource::getLocation);
    }

    public static Optional<URL> getJarUrlOpt(String resourcePath, ClassLoader classLoader) {
        return Optional.ofNullable(fromClasspath(resourcePath, classLoader))
                .map(URL::getPath)
                .filter(path -> path.endsWith(JAR_URL_SEPARATOR))
                .map(path -> path.substring(0, path.length() - resourcePath.length() - 1))
                .map(UrlUtil::from);
    }

    public static Optional<File> getJarDirFileOpt(URL jatUrl) {
        return Optional.ofNullable(jatUrl)
                .map(URL::getPath)
                // 处理路径中特殊字符（如：中文）部分
                .map(ThrowableUtil.sneakyThrow(new ThrowableUtil.ThrowThrowableFunction<String, String>() {
                    @Override
                    public String apply(String path) throws Throwable {
                        return URLDecoder.decode(path, FileUtil.getDefaultEncoding());
                    }
                }))
                .map(File::new)
                .map(File::getParentFile);
    }

    public static Optional<Path> getJarDirPathOpt(URL jatUrl) {
        return getJarDirFileOpt(jatUrl).map(File::toPath);
    }

    public static URLConnection openConnection(URL url) {
        return ObjectUtil.mapIfNonNull(url, ThrowableUtil.sneakyThrow(new ThrowableUtil.ThrowThrowableFunction<URL, URLConnection>() {
            @Override
            public URLConnection apply(URL url) throws Throwable {
                return url.openConnection();
            }
        }));
    }

    public static InputStream openStream(URL url) {
        return ObjectUtil.mapIfNonNull(url, ThrowableUtil.sneakyThrow(URL::openStream));
    }
}
