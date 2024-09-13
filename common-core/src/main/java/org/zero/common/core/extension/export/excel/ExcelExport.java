package org.zero.common.core.extension.export.excel;

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
     * 密码。不为空时生效。
     */
    String password() default "";
}
