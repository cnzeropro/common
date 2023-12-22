package org.zero.common.core.util.java.file;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/16
 */
class EncodingDetectTest {

    @Test
    void test() {
        String filePath = "C:\\Users\\Zero\\Desktop\\test.docx";
        String encode = EncodingDetect.getJavaEncode(filePath);
        System.out.println(encode);
    }
}