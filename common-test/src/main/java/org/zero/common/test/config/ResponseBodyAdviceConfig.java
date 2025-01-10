package org.zero.common.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zero.common.core.support.export.FileExportResponseBodyAdvice;
import org.zero.common.core.support.export.archive.ArchiveExportResponseBodyAdvice;
import org.zero.common.core.support.export.excel.easyexcel.EasyExcelResponseBodyAdvice;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/13
 */
@Import({
    FileExportResponseBodyAdvice.class,
    ArchiveExportResponseBodyAdvice.class,
    EasyExcelResponseBodyAdvice.class,
})
@Configuration(proxyBeanMethods = false)
public class ResponseBodyAdviceConfig {
}
