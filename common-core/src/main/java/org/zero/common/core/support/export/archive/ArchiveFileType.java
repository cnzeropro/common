package org.zero.common.core.support.export.archive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zero.common.core.support.export.BaseFileType;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/5
 */
@Getter
@AllArgsConstructor
public enum ArchiveFileType implements BaseFileType {
    ZIP("zip", "application/zip"),
    GZIP("gz", "application/gzip"),
    ZLIB("zlib", "application/zlib"),
    /* ********************************************* 以下还未支持 ********************************************* */
    RAR("rar", "application/vnd.rar"),
    X_7Z("7z", "application/x-7z-compressed"),
    BZIP("bz", "application/x-bzip"),
    BZIP2("bz2", "application/x-bzip2"),
    TAR("tar", "application/x-tar"),
    ARCHIVE("arc", "application/x-freearc"),
    JAR("jar", "application/java-archive"),
    ;

    private final String extName;
    private final String contentType;
}
