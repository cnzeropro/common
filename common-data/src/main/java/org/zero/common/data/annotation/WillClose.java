package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 指示被标注的资源将在当前作用域内被关闭。
 * <p>
 * 用于参数或局部变量上，声明调用方或当前方法会负责关闭该资源（如流、连接等）。
 * 与 {@link WillNotClose} 互为反向声明，配合使用可避免资源泄漏或重复关闭。
 * <p>
 * 该注解仅作为文档和静态分析标记，不改变运行时行为。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Retention(RetentionPolicy.CLASS)
@Target({PARAMETER, LOCAL_VARIABLE, TYPE_PARAMETER, TYPE_USE})
@Documented
public @interface WillClose {
}
