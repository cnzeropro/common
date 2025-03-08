package org.zero.common.test.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.support.export.FileExport;
import org.zero.common.core.support.export.archive.ArchiveExport;
import org.zero.common.core.support.export.excel.ExcelExport;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @see org.zero.common.test.config.ResponseBodyAdviceConfig
 * @since 2024/9/13
 */
@RestController
@RequestMapping("export")
public class ExportController {
    /**
     * @see org.zero.common.core.support.export.FileExportResponseBodyAdvice
     */
    @FileExport
    @GetMapping("e1")
    public String e1() {
        return "D:\\Zero\\新建 文本文档.txt";
    }

    /**
     * @see org.zero.common.core.support.export.excel.easyexcel.EasyExcelResponseBodyAdvice
     */
    @ExcelExport
    @GetMapping("e2")
    public List<Map<String, Object>> e2() {
        return ListUtil.of(MapBuilder.<String, Object>create(true)
                        .put("标题1", "hello")
                        .put("标题2", 34L)
                        .build(),
                MapBuilder.<String, Object>create(true)
                        .put("标题1", 1)
                        .put("标题2", LocalDateTime.now())
                        .build(),
                MapBuilder.<String, Object>create(true)
                        .put("标题1", new Date())
                        .put("标题2", 2D)
                        .build());
    }

    /**
     * @see org.zero.common.core.support.export.archive.ArchiveExportResponseBodyAdvice
     */
    @ArchiveExport
    @GetMapping("e3")
    public String[] e3() {
        // return new String[]{
        //         "/root/a.txt",
        //         "d:/Users/Zero",
        //         "C:\\Users\\Zero\\Desktop\\1.txt",
        //         "classpath:/sqls/insert.sql",
        //         "file:///f:\\Download\\1.txt",
        //         "http://www.example.com",
        //         "abcdefg",
        //         "https://www.example.com",
        //         "ftp://example.com/resource.txt",
        // };
        return new String[]{
                "D:/Zero/新建 文本文档.txt",
                "file:///D:\\Zero\\新建文件夹\\",
        };
    }
}
