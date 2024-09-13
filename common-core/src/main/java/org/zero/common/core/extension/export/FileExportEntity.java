package org.zero.common.core.extension.export;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.FastByteArrayOutputStream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/10
 */
@Getter
@Setter
public class FileExportEntity extends CustomFileType {
    private String filename;
    private FastByteArrayOutputStream fastByteArrayOutputStream;

    protected FileExportEntity(FastByteArrayOutputStream fastByteArrayOutputStream, String extName, String contentType) {
        super(extName, contentType);
        this.fastByteArrayOutputStream = fastByteArrayOutputStream;
    }

    public static FileExportEntity of(String filename, FastByteArrayOutputStream fastByteArrayOutputStream, String extName, String contentType) {
        FileExportEntity fileExportEntity = new FileExportEntity(fastByteArrayOutputStream, extName, contentType);
        fileExportEntity.setFilename(filename);
        return fileExportEntity;
    }

    public static FileExportEntity of(String filename, FastByteArrayOutputStream fastByteArrayOutputStream, BaseFileType baseFileType) {
        return of(filename, fastByteArrayOutputStream, baseFileType.getExtName(), baseFileType.getContentType());
    }

    public static FileExportEntity of(FastByteArrayOutputStream fastByteArrayOutputStream, BaseFileType baseFileType) {
        return new FileExportEntity(fastByteArrayOutputStream, baseFileType.getExtName(), baseFileType.getContentType());
    }

    public static FileExportEntity of(BaseFileType baseFileType) {
        return new FileExportEntity(null, baseFileType.getExtName(), baseFileType.getContentType());
    }
}
