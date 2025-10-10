package org.zero.common.core.support.export.csv;

import org.springframework.core.annotation.AliasFor;

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
public @interface CsvExport {
    @AliasFor("filename")
    String value() default "";

    /**
     * 文件名。如果为空，则默认为【当前时间.文件类型后缀名】
     */
    @AliasFor("value")
    String filename() default "";

    /**
     * 文件类型。默认：CSV
     */
    CsvFileType fileType() default CsvFileType.CSV;

    /**
     * 分隔符。默认：,
     */
    String delimiter() default ",";

    /**
     * 是否写入表头。默认：true
     */
    boolean withHeader() default true;

    /**
     * 是否添加 BOM。默认：false
     */
    boolean withBom() default false;

    /**
     * 字符集。默认：UTF-8
     */
    String charset() default "UTF-8";
}
