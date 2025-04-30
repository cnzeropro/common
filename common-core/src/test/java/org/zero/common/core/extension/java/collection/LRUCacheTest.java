package org.zero.common.core.extension.java.collection;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.util.LRUCache;

import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/16
 */
class LRUCacheTest {

    @Test
    void test() {
        Map<String, String> cache = LRUCache.createThreadSafe(3);
        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        System.out.println(cache);
        // 移除最近最少使用的元素
        cache.put("4", "4");
        System.out.println(cache);
    }
}