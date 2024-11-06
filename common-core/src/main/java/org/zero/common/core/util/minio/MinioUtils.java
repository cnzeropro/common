package org.zero.common.core.util.minio;

import cn.hutool.core.io.FileUtil;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.zero.common.core.util.spring.SpringUtils;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero
 * @since 2021/10/20 13:36
 */
@Slf4j
@UtilityClass
public class MinioUtils {
    public static final int PART_SIZE = 5;

    @SneakyThrows(Exception.class)
    public static boolean bucketExists(String bucket) {
        return getClient().bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    }

    @SneakyThrows(Exception.class)
    public static void makeBucket(String bucket) {
        if (!bucketExists(bucket)) {
            getClient().makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @SneakyThrows(Exception.class)
    public static void putObject(String bucket, String path, String name, String contentType, InputStream in) {
        String object = path + name;
        if (!StringUtils.hasText(contentType)) {
            contentType = FileUtil.getMimeType(name);
        }
        getClient().putObject(PutObjectArgs.builder()
                .bucket(bucket).object(object)
                .contentType(contentType)
                // 请勿使用in.available()，可能会造成读取不准确
                .stream(in, -1, DataSize.ofMegabytes(PART_SIZE).toBytes())
                .build());
    }

    @SneakyThrows(Exception.class)
    public static void putObject(String bucket, String path, String name, MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.warn("The foreground upload file is empty.");
            return;
        }
        String contentType = multipartFile.getContentType();
        putObject(bucket, path, name, contentType, multipartFile.getInputStream());
    }

    @SneakyThrows(Exception.class)
    public static InputStream getObject(String bucket, String object) {
        return getClient().getObject(GetObjectArgs.builder().bucket(bucket).object(object).build());
    }

    /**
     * 获取对象外链
     */
    @SneakyThrows(Exception.class)
    public static String getObjectUrl(String bucket, String object) {
        return getClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucket).object(object).expiry(7, TimeUnit.DAYS).build());
    }

    /* **************************************************** Private **************************************************** */
    private static MinioClient minioClient;

    private static MinioClient getClient() {
        if (Objects.isNull(minioClient)) {
            synchronized (MinioUtils.class) {
                if (Objects.isNull(minioClient)) {
                    minioClient = SpringUtils.getBean(MinioClient.class);
                }
            }
        }
        return minioClient;
    }
}
