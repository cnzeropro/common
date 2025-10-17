package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zero.common.data.enumeration.BaseFileType;
import org.zero.common.data.enumeration.CacheControlInstruction;
import org.zero.common.data.enumeration.CacheStrategy;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadQO implements Serializable {
	private BaseFileType fileType;
	private String fileName;
	private CacheStrategy cacheStrategy = CacheControlInstruction.NO_CACHE;

	public DownloadQO(BaseFileType fileType, String fileName) {
		this.fileType = fileType;
		this.fileName = fileName;
	}
}
