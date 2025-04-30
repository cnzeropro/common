package org.zero.common.core.util.alibaba.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.zero.common.core.util.java.reflect.ClassUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2019/12/23
 */
public class TransmittableHolder {
    protected static final TransmittableThreadLocal<Map<String, Object>> holder = TransmittableThreadLocal.withInitial(HashMap::new);

    public static void setVal(String key, Object val) {
        getContext().put(key, val);
    }

    public static Object getVal(String key) {
        return getContext().get(key);
    }

    public static <T> T getVal(String key, Type type) {
        Object val = getVal(key);
        return ClassUtil.cast(val, type);
    }

    public static void remove(String... keys) {
        Map<String, Object> map = getContext();
        for (String key : keys) {
            map.remove(key);
        }
    }

    public static void clear() {
        getContext().clear();
    }

    public static void setContext(Map<String, Object> context) {
        holder.set(context);
    }

    protected static Map<String, Object> getContext() {
        return holder.get();
    }
}
