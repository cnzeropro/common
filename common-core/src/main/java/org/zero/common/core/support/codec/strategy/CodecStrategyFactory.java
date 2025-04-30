package org.zero.common.core.support.codec.strategy;

import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.ReflectUtil;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.MapCache;
import org.zero.common.core.util.java.reflect.MethodUtil;
import org.zero.common.data.exception.CommonException;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/25
 */
public class CodecStrategyFactory {
    protected static final Cache<String, Class<? extends CodecStrategy>> CACHE = MapCache.of(CaseInsensitiveMap::new);

    static {
        CACHE.put("AES", AESCodec.class);
        CACHE.put("DESede", DESedeCodec.class);
        CACHE.put("DES", DESCodec.class);
        CACHE.put("SM4", SM4Codec.class);
    }

    public static synchronized void add(String algorithm, Class<? extends CodecStrategy> clazz) {
        CACHE.put(algorithm, clazz);
    }

    public static CodecStrategy get(String algorithm, StrategyContext context) {
        Class<? extends CodecStrategy> clazz = CACHE.get(algorithm);
        if (Objects.isNull(clazz)) {
            throw new CommonException("unsupported algorithm: " + algorithm);
        }
        try {
            return ReflectUtil.newInstance(clazz, context);
        } catch (Exception ignored) {
            return MethodUtil.getPublicMethods(clazz)
                    .stream()
                    .filter(MethodUtil::isBuilderMethod)
                    .findFirst()
                    .<CodecStrategy>map(method -> MethodUtil.invokeStatic(method, clazz, context))
                    .orElseThrow(() -> new CommonException("Cannot instantiate: " + clazz));
        }
    }

    protected CodecStrategyFactory () {
        throw new UnsupportedOperationException();
    }
}
