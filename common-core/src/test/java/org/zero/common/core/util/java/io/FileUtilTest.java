package org.zero.common.core.util.java.io;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/24
 */
@Log
class FileUtilTest {

    @Test
    void from() {
        // String path = "classpath:/ip2region.xdb";
        String path = "ip2region.xdb";
        File file = FileUtil.newFile(path);
        log.log(Level.INFO, "file: {0}", file);
    }

    @Test
    void getUserHomeDirPath() {
        String userHomePath = FileUtil.getUserHomeDirPath();
        log.log(Level.INFO, "userHomePath: {0}", userHomePath);
    }

    @Test
    void getTmpDirPath() {
        String tmpDirPath = FileUtil.getTmpDirPath();
        log.log(Level.INFO, "tmpDirPath: {0}", tmpDirPath);
    }

    @Test
    void isAbsolute() {
    }

    @Test
    void isRelative() {
    }

    @Test
    void normalize() {
    }
}