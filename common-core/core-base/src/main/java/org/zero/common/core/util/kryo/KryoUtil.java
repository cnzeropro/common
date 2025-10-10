package org.zero.common.core.util.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import lombok.experimental.UtilityClass;
import org.zero.common.core.util.java.lang.ClassLoaderUtil;
import org.zero.common.core.util.java.lang.ClassUtil;

import java.lang.reflect.Type;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@UtilityClass
public class KryoUtil {
    private static final ThreadLocal<Kryo> kryoLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 支持对象循环引用，虽然会导致性能些许下降，但为了防止栈溢出错误，开启还是很有必要的
        kryo.setReferences(true);
        // 关闭序列化注册，虽然会导致性能些许下降，但在分布式环境中，注册类生成 ID 不一致会导致错误，因此需要关闭
        kryo.setRegistrationRequired(false);
        // 支持删除或者新增字段
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        // 设置类加载器为线程上下文类加载器
        kryo.setClassLoader(ClassLoaderUtil.get());
        return kryo;
    });

    public static byte[] serialize(Object obj) {
        Kryo kryo = kryoLocal.get();
        try (Output output = new Output(4096, -1)) {
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return output.getBuffer();
        }
    }

    public static Object deserialize(byte[] buffer) {
        Kryo kryo = kryoLocal.get();
        return kryo.readClassAndObject(new Input(buffer));
    }

    public static <T> T deserialize(byte[] buffer, Type type) {
        Object deserialized = deserialize(buffer);
        return ClassUtil.cast(deserialized, type);
    }
}
