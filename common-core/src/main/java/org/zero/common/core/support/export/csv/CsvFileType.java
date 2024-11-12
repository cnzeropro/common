package org.zero.common.core.support.export.csv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zero.common.core.support.export.BaseFileType;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/5
 */
@Getter
@AllArgsConstructor
public enum CsvFileType implements BaseFileType {
    CSV("csv", "text/csv"),
    ;

    private final String extName;
    private final String contentType;
}
