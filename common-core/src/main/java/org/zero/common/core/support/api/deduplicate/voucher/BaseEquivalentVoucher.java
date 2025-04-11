package org.zero.common.core.support.api.deduplicate.voucher;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import lombok.Cleanup;
import org.springframework.util.DigestUtils;
import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
@FunctionalInterface
public interface BaseEquivalentVoucher extends EquivalentVoucher {
    @Override
    default String generate(Object context, Deduplicate deduplicate) {
        Object equivalentVoucher = this.create(context, deduplicate);
        byte[] serialized = this.serialize(equivalentVoucher);
        return this.visualize(serialized);
    }

    /**
     * 创建唯一凭证
     *
     * @param context       上下文。在拦截器中为 {@linkplain org.springframework.web.method.HandlerMethod HandlerMethod}，在切面中为 {@linkplain org.aspectj.lang.JoinPoint JoinPoint}
     * @param deduplicate 防重注解
     * @return 唯一凭证
     */
    Object create(Object context, Deduplicate deduplicate);

    /**
     * 将凭证（{@link Object}）序列化（{@code byte[]}）
     *
     * @param source 凭证
     * @return 序列化后的凭证
     */
    default byte[] serialize(Object source) {
        if (Objects.isNull(source)) {
            return new byte[0];
        }
        // Kryo 线程不安全
        Kryo kryo = new Kryo();
        @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        @Cleanup Output output = new Output(outputStream);
        kryo.register(source.getClass());
        kryo.writeObject(output, source);
        output.flush();
        return outputStream.toByteArray();
    }

    /**
     * 将序列化凭证（{@code byte[]}）可视化（{@link String}）
     *
     * @param source 序列化凭证
     * @return 可视化后的凭证
     */
    default String visualize(byte[] source) {
        return DigestUtils.md5DigestAsHex(source);
    }
}
