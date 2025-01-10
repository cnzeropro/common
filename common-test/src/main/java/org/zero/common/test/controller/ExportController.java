package org.zero.common.test.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.aop.aspect.log.LogLevel;
import org.zero.common.core.aop.aspect.log.TraceLog;
import org.zero.common.core.support.export.FileExport;
import org.zero.common.core.support.export.archive.ArchiveExport;
import org.zero.common.core.support.export.excel.ExcelExport;

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
    @TraceLog(LogLevel.INFO)
    @FileExport
    @GetMapping("e1")
    public String e1() {
        return "C:\\Users\\Rongan\\Desktop\\其他.txt";
        // return "file:///C:/Users/Rongan/Desktop/其他.txt";
    }

    @TraceLog(LogLevel.INFO)
    @ExcelExport
    @GetMapping("e2")
    public List<Map<String, Object>> e2() {
        return ListUtil.of(MapBuilder.<String, Object>create(true)
                        .put("标题1", "hello")
                        .put("标题2", 34L)
                        .build(),
                MapBuilder.<String, Object>create(true)
                        .put("标题1", 1)
                        .put("标题2", 2D)
                        .build());
    }

    @ArchiveExport
    @GetMapping("e3")
    public List<String> e3() {
        return ListUtil.of("C:\\Users\\Rongan\\Desktop\\其他.txt");
    }
}
