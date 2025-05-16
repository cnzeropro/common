package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadQO implements Serializable {
    private FileType fileType;
    private String fileName;
    private CacheStrategy cacheStrategy;
}
