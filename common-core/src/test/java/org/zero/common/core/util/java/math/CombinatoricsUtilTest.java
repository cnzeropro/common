package org.zero.common.core.util.java.math;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.lang.CombinatoricsUtil;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/6/30 14:56
 */
class CombinatoricsUtilTest {
    @Test
    void getCombinatoricsArray() {
        String source = "3-1-6";
        // String source = "veU-**&-78i-fcw-y?s-aa-n9hw-bb-l8n-o09-#Qm-^7m-t4g-E$5rf-[H,po)-2n4q7-2qD6v-ii9";
        String[] combinatoricsArray = CombinatoricsUtil.getCombinatoricsArray(source, "-", 3);
        int size = combinatoricsArray.length;
        int num = size / 1000;
        for (int i = 0; i < size; i++) {
            if (num != 0 && i % num == 0) {
                System.out.println();
            }
            System.out.print(combinatoricsArray[i] + " ");
        }
        System.out.println("\n共" + size + "种组合，如上");
    }

    @Test
    void getCombinatoricsSet() {
        String source = "9&2&7&5";
        Set<String> combinatoricsSet = CombinatoricsUtil.getCombinatoricsSet(source, "&", 3);
        // 排序
        TreeSet<String> treeSet = new TreeSet<>(combinatoricsSet);
        System.out.println(treeSet);
    }
}