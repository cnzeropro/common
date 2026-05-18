/**
 * 基于 {@code javax.validation} 的电话号码格式约束。
 * <p>
 * Spring Boot 2 / Javax 生态使用本包；Spring Boot 3 / Jakarta 生态请使用
 * {@code org.zero.common.core.extension.jakarta.validation.validator}。
 * <p>
 * 本包提供 {@link org.zero.common.core.extension.javax.validation.validator.Mobile}、{@link org.zero.common.core.extension.javax.validation.validator.Landline} 与 {@link org.zero.common.core.extension.javax.validation.validator.Phone} 三个格式约束。约束默认允许 {@code null}、
 * 空字符串及纯空白字符串通过；字段必填请额外组合 {@code @NotBlank}。
 * <p>
 * 默认消息模板使用短消息键，例如 {@code javax.validation.validator.Mobile.message}。本模块在 classpath 根路径提供
 * 标准 {@code ValidationMessages*.properties} 资源包，并只维护 {@code javax.validation.validator.*} 消息键。
 */
package org.zero.common.core.extension.javax.validation.validator;
