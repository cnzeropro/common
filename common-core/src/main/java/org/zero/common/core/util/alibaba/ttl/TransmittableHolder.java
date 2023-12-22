package org.zero.common.core.util.alibaba.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2019/12/23
 */
@UtilityClass
public class TransmittableHolder {
    private static final TransmittableThreadLocal<Map<String, Object>> holder = TransmittableThreadLocal.withInitial(HashMap::new);

    public static void setVal(String key, Object val) {
        getMap().put(key, val);
    }

    public static Object getVal(String key) {
        return getMap().get(key);
    }

    public static <T> T getVal(String key, Class<T> type) {
        return type.cast(getVal(key));
    }

    public static void remove(String... keys) {
        if (Objects.isNull(keys) || keys.length == 0) {
            removeAll();
            return;
        }

        for (String key : keys) {
            removeOne(key);
        }
    }

    public static void removeOne(String key) {
        getMap().remove(key);
    }

    public static void removeAll() {
        getMap().clear();
    }

    private static Map<String, Object> getMap() {
        return holder.get();
    }
}
