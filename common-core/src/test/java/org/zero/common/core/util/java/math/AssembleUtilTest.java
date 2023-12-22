package org.zero.common.core.util.java.math;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/6/30 14:56
 */
class AssembleUtilTest {
    @Test
    void test() {
        String source = "veU-**&-78i-fcw-y?s-aa-n9hw-bb-l8n-o09-#Qm-^7m-t4g-E$5rf-[H,po)-2n4q7-2qD6v-ii9";
        String[] assemble = AssembleUtil.getAssemble(source, "-", 6);
        int size = assemble.length;
        int num = size / 1000;
        for (int i = 0; i < size; i++) {
            if (num != 0 && i % num == 0) {
                System.out.println();
            }
            System.out.print(assemble[i] + " ");
        }
        System.out.println("\n共" + size + "种组合，如上");
    }
}