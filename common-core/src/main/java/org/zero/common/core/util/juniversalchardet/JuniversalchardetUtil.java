package org.zero.common.core.util.juniversalchardet;

import lombok.SneakyThrows;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2016/2/1
 */
public class JuniversalchardetUtil {
    public static final int DEFAULT_DETECT_SIZE = 4096;

    public static String detectEncoding(byte[] content) {
        return detectEncoding(content, content.length);
    }

    public static String detectEncoding(byte[] content, int detectSize) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(content, 0, detectSize);
        detector.dataEnd();
        return detector.getDetectedCharset();
    }

    public static String detectEncoding(InputStream inputStream) {
        return detectEncoding(inputStream, DEFAULT_DETECT_SIZE);
    }

    @SneakyThrows
    public static String detectEncoding(InputStream inputStream, int detectSize) {
        byte[] buffer = new byte[detectSize];
        boolean canMark = inputStream.markSupported();
        if (canMark) {
            inputStream.mark(detectSize);
        }
        int readSize = inputStream.read(buffer);
        if (readSize < 1) {
            return null;
        }
        if (canMark) {
            inputStream.reset();
        }
        return detectEncoding(buffer, readSize);
    }

    public static String detectEncoding(File file) {
        return detectEncoding(file, DEFAULT_DETECT_SIZE);
    }

    public static String detectEncoding(File file, int detectSize) {
        return detectEncoding(file.toPath(), detectSize);
    }

    public static String detectEncoding(Path path) {
        return detectEncoding(path, DEFAULT_DETECT_SIZE);
    }

    public static String detectEncoding(Path path, int detectSize) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return detectEncoding(inputStream, detectSize);
        } catch (Exception ignored) {
            return null;
        }
    }
}
