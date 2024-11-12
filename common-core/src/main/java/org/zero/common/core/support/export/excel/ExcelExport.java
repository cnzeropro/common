package org.zero.common.core.support.export.excel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/4
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ExcelExport {
    /**
     * 文件名。如果为空，则默认为【当前时间.文件类型后缀名】
     */
    String filename() default "";

    /**
     * 文件类型。默认：XLSX
     */
    ExcelFileType fileType() default ExcelFileType.XLSX;

    /**
     * 是否写入表头。默认：true
     */
    boolean withHeader() default true;

    /**
     * 是否都在在内存中处理，如果为 true 表示 excel 文件生成都在内存中完成，反之表示使用磁盘进行大文件生成。默认：false。仅 xlsx（Excel07 版本）支持
     */
    boolean inMemory() default false;

    /**
     * 密码。不为空时生效。
     */
    String password() default "";
}
