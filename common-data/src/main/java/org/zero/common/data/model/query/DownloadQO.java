package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zero.common.data.enumeration.BaseFileType;
import org.zero.common.data.enumeration.CacheControlInstruction;
import org.zero.common.data.enumeration.CacheStrategy;

import java.io.Serializable;

/**
 * 文件下载查询参数对象
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadQO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件类型
     */
    private BaseFileType fileType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 缓存策略，默认不缓存
     */
    private CacheStrategy cacheStrategy = CacheControlInstruction.NO_CACHE;

	public DownloadQO(BaseFileType fileType, String fileName) {
		this.fileType = fileType;
		this.fileName = fileName;
	}
}
