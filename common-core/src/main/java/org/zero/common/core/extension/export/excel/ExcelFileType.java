package org.zero.common.core.extension.export.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zero.common.core.extension.export.BaseFileType;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/5
 */
@Getter
@AllArgsConstructor
public enum ExcelFileType implements BaseFileType {
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    XLS("xls", "application/vnd.ms-excel"),
    ;

    private final String extName;
    private final String contentType;
}
