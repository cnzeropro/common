package org.zero.common.core.support.api.deduplicate.voucher;

import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;

/**
 * 等效凭证
 * <p>
 * 用于判别是否为重复请求
 * <p>
 * 可以使用请求头、请求参数、请求体、请求 cookie、请求 ip、请求 url、请求方法、请求头等等参数当凭证，也可以把它们组合成一个凭证。<br>
 * 生成凭证的过程，本质上是一个序列化的过程（把 {@link Object} 转成 {@link String}）。<br>
 * 但不建议使用 {@linkplain Object#toString() toString} 方法，因为不是所有的对象都能提供规范的 {@code toString} 方法。<br>
 * 也不建议使用 {@linkplain java.io.ObjectOutputStream#writeObject(Object) JDK Serialization} 进行序列化，同理，也不是所有的对象都能提供规范的序列化标准（实现 {@linkplain java.io.Serializable Serializable} 接口）。
 * 因此，建议使用 {@link BaseEquivalentVoucher} 来生成凭证，此类当中有一些默认的实现，提供了基于 {@code Kryo} 序列化的方案，但注意引入相关依赖
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
@FunctionalInterface
public interface EquivalentVoucher {
    /**
     * 生成凭证
     *
     * @param context       上下文。在拦截器中为 {@linkplain org.springframework.web.method.HandlerMethod HandlerMethod}，在切面中为 {@linkplain org.aspectj.lang.JoinPoint JoinPoint}
     * @param deduplicate 防重注解
     * @return 凭证，判别何为重复
     */
    String generate(Object context, Deduplicate deduplicate);
}
