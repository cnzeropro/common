package org.zero.common.core.util.java.io;

import lombok.SneakyThrows;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.core.util.java.net.UrlUtil;
import org.zero.common.core.util.java.reflect.ClassUtil;
import org.zero.common.core.util.java.util.CollectionUtil;
import org.zero.common.data.constant.CharPool;
import org.zero.common.data.constant.StringPool;
import org.zero.common.data.exception.UtilException;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/24
 */
public class FileUtil {
    /**
     * Class 文件扩展名
     */
    public static final String CLASS_EXT = ClassUtil.CLASS_FILE_SUFFIX;
    /**
     * 文件路径分隔符
     * <p>
     * 在 Unix 和 Linux 下 是{@code '/'}; 在 Windows 下是 {@code '\'}
     */
    public static final String FILE_SEPARATOR = File.separator;
    /**
     * 多个 PATH 之间的分隔符
     * <p>
     * 在 Unix 和 Linux 下 是{@code ':'}; 在 Windows 下是 {@code ';'}
     */
    public static final String PATH_SEPARATOR = File.pathSeparator;

    /**
     * 绝对路径判断正则
     */
    private static final Pattern PATTERN_PATH_ABSOLUTE = Pattern.compile("^[a-zA-Z]:([/\\\\].*)?", Pattern.DOTALL);

    public static File from(String path) {
        String normalizedPath = normalize(path);
        if (isAbsolute(normalizedPath)) {
            return newFile(normalizedPath);
        }
        Optional<URL> jarUrlOpt = UrlUtil.getJarUrlOpt();
        if (jarUrlOpt.isPresent()) {
            Optional<String> jarDirOpt = UrlUtil.getJarDirFileOpt(jarUrlOpt.get()).map(File::getAbsolutePath);
            if (jarDirOpt.isPresent()) {
                return newFile(jarDirOpt.get(), normalizedPath);
            }
        }
        throw new UtilException("Can not get file from path: " + path);
    }

    public static File from(URI uri) {
        return ObjectUtil.mapIfNonNull(uri, File::new);
    }

    @SneakyThrows
    public static File from(URL url) {
        return ObjectUtil.mapIfNonNull(url, u -> from(ThrowableUtil.sneakyThrow(u, URL::toURI)));
    }

    public static File newFile(String... subPaths) {
        if (ArrayUtil.isEmpty(subPaths)) {
            return null;
        }
        String path = Arrays.stream(subPaths)
                .filter(CharSequenceUtil::nonEmpty)
                .map(StringUtil::trim)
                .collect(Collectors.joining(StringPool.SLASH));
        String normalizedPath = FileUtil.normalize(path);
        return ObjectUtil.mapIfNonNull(normalizedPath, File::new);
    }

    public static String getUserHomeDirPath() {
        return System.getProperty("user.home");
    }

    public static String getTmpDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 给定路径已经是绝对路径
     * <p>
     * 此方法并没有针对路径做标准化，建议先执行{@link #normalize(String)}方法标准化路径后判断<br>
     * 绝对路径判断条件是：
     * <ul>
     *     <li>以/开头的路径</li>
     *     <li>满足类似于 c:/xxx，其中层级随意，不区分大小写</li>
     *     <li>满足类似于 d:\xxx，其中层级随意，不区分大小写</li>
     * </ul>
     *
     * @param path 需要检查的Path
     * @return 是否已经是绝对路径
     */
    public static boolean isAbsolute(final String path) {
        if (CharSequenceUtil.isEmpty(path)) {
            return false;
        }
        return CharPool.SLASH == path.charAt(0) || PATTERN_PATH_ABSOLUTE.matcher(path).matches();
    }

    public static boolean isRelative(final String path) {
        return !isAbsolute(path);
    }

    /**
     * 修复路径
     * <p>
     * 如果原路径尾部有分隔符，则保留为标准分隔符（/），否则不保留
     * <ol>
     * <li>统一用 /</li>
     * <li>多个 / 转换为一个 /</li>
     * <li>去除左边空格</li>
     * <li>.. 和 . 转换为绝对路径，当..多于已有路径时，直接返回根路径</li>
     * </ol>
     * like：
     * <pre>
     * "/foo//" =》 "/foo/"
     * "/foo/./" =》 "/foo/"
     * "/foo/../bar" =》 "/bar"
     * "/foo/../bar/" =》 "/bar/"
     * "/foo/../bar/../baz" =》 "/baz"
     * "/../" =》 "/"
     * "foo/bar/.." =》 "foo"
     * "foo/../bar" =》 "bar"
     * "foo/../../bar" =》 "bar"
     * "//server/foo/../bar" =》 "/server/bar"
     * "//server/../bar" =》 "/bar"
     * "C:\\foo\\..\\bar" =》 "C:/bar"
     * "C:\\..\\bar" =》 "C:/bar"
     * "~/foo/../bar/" =》 "~/bar/"
     * "~/../bar" =》 普通用户运行是'bar的home目录'，ROOT用户运行是'/bar'
     * </pre>
     *
     * @param path 原路径
     * @return 修复后的路径
     */
    public static String normalize(final String path) {
        if (CharSequenceUtil.isNull(path)) {
            return null;
        }

        // 兼容 Windows 下的共享目录路径（原始路径如果以\\开头，则保留这种路径）
        if (path.startsWith("\\\\")) {
            return path;
        }

        // 兼容 Spring 风格的 ClassPath 路径，去除前缀，不区分大小写
        String pathToUse = StringUtil.removePrefix(path, UrlUtil.URL_PREFIX_CLASSPATH, true);
        // 去除 file: 前缀
        pathToUse = StringUtil.removePrefix(pathToUse, UrlUtil.FILE_URL_PREFIX, true);

        // 识别 home 目录形式，并转换为绝对路径
        if (StringUtil.startWith(pathToUse, "~", false)) {
            pathToUse = getUserHomeDirPath() + pathToUse.substring(1);
        }

        // 统一使用斜杠
        pathToUse = pathToUse.replaceAll("[/\\\\]+", StringPool.SLASH);
        // 去除开头空白符，末尾空白符合法，不去除
        pathToUse = StringUtil.trimStart(pathToUse);
        // 去除尾部的换行符
        pathToUse = StringUtil.trimEnd(pathToUse, c -> c == '\n' || c == '\r');

        String prefix = StringPool.EMPTY;
        int prefixIndex = pathToUse.indexOf(StringPool.COLON);
        if (prefixIndex > -1) {
            // 可能 Windows 风格路径
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (StringUtil.startWith(prefix, StringPool.SLASH, false)) {
                // 去除类似于 /C: 这类路径开头的斜杠
                prefix = prefix.substring(1);
            }
            if (!prefix.contains(StringPool.SLASH)) {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            } else {
                // 如果前缀中包含 /，说明非 Windows 风格 path
                prefix = StringPool.EMPTY;
            }
        }
        if (pathToUse.startsWith(StringPool.SLASH)) {
            prefix += StringPool.SLASH;
            pathToUse = pathToUse.substring(1);
        }

        List<String> pathList = StringUtil.split(pathToUse, StringPool.SLASH);
        List<String> pathElements = new LinkedList<>();
        int tops = 0;
        String element;
        for (int i = pathList.size() - 1; i >= 0; i--) {
            element = pathList.get(i);
            // 只处理非.的目录，即只处理非当前目录
            if (!StringPool.DOT.equals(element)) {
                if (StringPool.DOUBLE_DOT.equals(element)) {
                    tops++;
                } else {
                    if (tops > 0) {
                        // 有上级目录标记时按照个数依次跳过
                        tops--;
                    } else {
                        // 正常路径
                        pathElements.add(0, element);
                    }
                }
            }
        }
        if (tops > 0 && CharSequenceUtil.isEmpty(prefix)) {
            // 只有相对路径补充开头的..，绝对路径直接忽略之
            while (tops-- > 0) {
                // 遍历完节点发现还有上级标注（即开头有一个或多个..），补充之
                pathElements.add(0, StringPool.DOUBLE_DOT);
            }
        }
        return prefix + CollectionUtil.join(pathElements, StringPool.SLASH);
    }

    public static String getDefaultEncoding() {
        return System.getProperty("file.encoding", Charset.defaultCharset().name());
    }

    protected FileUtil() {
        throw new UnsupportedOperationException();
    }
}
