package org.zero.common.core.extension.export;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/10
 */
class FileExportResponseBodyAdviceTest {

    @Test
    void test() {
        String[] testStrings = {
                "/root/a.txt",
                "d:/Users/Zero",
                "C:\\Users\\Zero\\Desktop\\1.txt",
                "classpath:/sqls/insert.sql",
                "file:///f:\\Download\\1.txt",
                "http://www.example.com",
                "abcdefg",
                "https://www.example.com",
                "ftp://example.com/resource.txt"
        };
    }

}