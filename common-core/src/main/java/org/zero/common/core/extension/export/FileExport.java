package org.zero.common.core.extension.export;

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
public @interface FileExport {
    /**
     * 文件名。如果为空，则默认为【当前时间.文件类型后缀名】，对于无法判断文件类型的，则默认为【当前时间】
     */
    String filename() default "";

    /**
     * 字符集。默认：UTF-8。仅导出字符流 {@link java.io.Reader} 使用
     */
    String charset() default "UTF-8";
}
