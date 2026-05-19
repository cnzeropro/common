/**
 * 基于 {@code jakarta.validation} 的电话号码格式约束。
 * <p>
 * 本包面向 Spring Boot 3 / Jakarta 生态，语义与 {@code javax.validation} 适配层保持一致。
 * <p>
 * 本包提供 {@link Mobile}、{@link Landline} 与 {@link Phone} 三个格式约束。约束默认允许 {@code null}、
 * 空字符串及纯空白字符串通过；字段必填请额外组合 {@code @NotBlank}。
 * <p>
 * 默认消息模板使用短消息键，例如 {@code jakarta.validation.constraints.Mobile.message}。本模块在 classpath 根路径提供
 * 标准 {@code ValidationMessages*.properties} 资源包，并只维护 {@code jakarta.validation.constraints.*} 消息键。
 */
package org.zero.common.core.extension.jakarta.validation.constraints;
