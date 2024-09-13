package org.zero.common.core.extension.export;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/10
 */
@Getter
@Setter
public class CustomFileType implements BaseFileType {
    private String extName;
    private String contentType;

    protected CustomFileType(String extName, String contentType) {
        this.extName = extName;
        this.contentType = contentType;
    }

    public static CustomFileType of(BaseFileType baseFileType) {
        return new CustomFileType(baseFileType.getExtName(), baseFileType.getContentType());
    }

    public static CustomFileType of(String extName, String contentType) {
        return new CustomFileType(extName, contentType);
    }
}
