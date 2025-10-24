package org.zero.common.core.extension.java.collection;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.util.LRUMap;
import org.zero.common.core.util.java.lang.ArrayUtil;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/16
 */
class LRUMapTest {

    @Test
    void test() {
        Map<String, String> map = LRUMap.create(true,3);
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
        System.out.println(map);
        // 移除最近最少使用的元素
        map.get("1");
        map.put("4", "4");
        System.out.println(map);

		int i = (ArrayUtil.MAX_ARRAY_SIZE) * 2;
		System.out.println(i);
		ByteBuffer.allocate(ArrayUtil.MAX_ARRAY_SIZE);
	}
}