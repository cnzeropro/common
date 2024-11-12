package org.zero.common.core.support.export.archive;

import org.zero.common.core.support.export.FileExport;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.zip.ZipEntry;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/4
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ArchiveExport {
    /**
     * 文件名。如果为空，则默认为【当前时间.文件类型后缀名】
     */
    String filename() default "";

    /**
     * 文件类型。默认：ZIP
     * <p>
     * 注意：目前仅支持 ZIP、GZIP、ZLIB。如果想导出其他格式归档包，请使用 {@link FileExport} 注解直接导出存在的归档文件。
     * 另外如果想导出多个文件到归档包里，目前仅支持 ZIP 格式；一个文件归档可以使用其他格式
     */
    ArchiveFileType fileType() default ArchiveFileType.ZIP;

    /**
     * 字符集。默认：UTF-8。仅 ZIP 支持
     */
    String charset() default "UTF-8";

    /**
     * 是否包含被打包目录，只针对压缩目录有效。默认：false。若为 false，则只压缩目录下的文件或目录，为 true 则将本目录也压缩。仅 ZIP 支持
     */
    boolean withSrcDir() default false;

    /**
     * 压缩级别。可选：1-9。默认：-1（系统默认压缩级别）。仅 ZIP 和 ZLIB 支持
     */
    int level() default -1;

    /**
     * 注释。仅 ZIP 支持
     */
    String comment() default "";

    /**
     * 压缩方式。可选：STORED（不压缩），DEFLATED（压缩）。默认：DEFLATED。仅 ZIP 支持
     */
    int method() default ZipEntry.DEFLATED;
}
