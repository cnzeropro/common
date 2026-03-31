/**
 * 通用标记注解。
 * <p>
 * 该包内注解主要用于<strong>编译期/文档期</strong>的语义补充，例如：
 * <ul>
 *     <li>空值语义声明（{@link org.zero.common.data.annotation.Null} / {@link org.zero.common.data.annotation.NonNull}）</li>
 *     <li>线程安全声明（{@link org.zero.common.data.annotation.ThreadSafe} / {@link org.zero.common.data.annotation.NotThreadSafe}）</li>
 *     <li>资源关闭责任声明（{@link org.zero.common.data.annotation.WillClose} / {@link org.zero.common.data.annotation.WillNotClose}）</li>
 *     <li>声明可能抛出的异常（{@link org.zero.common.data.annotation.Throw}）</li>
 *     <li>声明类型所需依赖（{@link org.zero.common.data.annotation.NeedLib}）</li>
 * </ul>
 * 以上注解均为 {@link java.lang.annotation.Retention @Retention}({@link java.lang.annotation.RetentionPolicy#CLASS CLASS}) 或文档标记，不改变运行时行为。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/3/31
 */
package org.zero.common.data.annotation;
