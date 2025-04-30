package org.zero.common.core.util.minio;

import io.minio.BucketExistsArgs;
import io.minio.CloseableIterator;
import io.minio.ComposeObjectArgs;
import io.minio.CopyObjectArgs;
import io.minio.DeleteBucketEncryptionArgs;
import io.minio.DeleteBucketLifecycleArgs;
import io.minio.DeleteBucketNotificationArgs;
import io.minio.DeleteBucketPolicyArgs;
import io.minio.DeleteBucketReplicationArgs;
import io.minio.DeleteBucketTagsArgs;
import io.minio.DeleteObjectLockConfigurationArgs;
import io.minio.DeleteObjectTagsArgs;
import io.minio.DisableObjectLegalHoldArgs;
import io.minio.DownloadObjectArgs;
import io.minio.EnableObjectLegalHoldArgs;
import io.minio.GetBucketEncryptionArgs;
import io.minio.GetBucketLifecycleArgs;
import io.minio.GetBucketNotificationArgs;
import io.minio.GetBucketPolicyArgs;
import io.minio.GetBucketReplicationArgs;
import io.minio.GetBucketTagsArgs;
import io.minio.GetBucketVersioningArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectLockConfigurationArgs;
import io.minio.GetObjectResponse;
import io.minio.GetObjectRetentionArgs;
import io.minio.GetObjectTagsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.IsObjectLegalHoldEnabledArgs;
import io.minio.ListBucketsArgs;
import io.minio.ListObjectsArgs;
import io.minio.ListenBucketNotificationArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PostPolicy;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.RestoreObjectArgs;
import io.minio.Result;
import io.minio.SelectObjectContentArgs;
import io.minio.SelectResponseStream;
import io.minio.SetBucketEncryptionArgs;
import io.minio.SetBucketLifecycleArgs;
import io.minio.SetBucketNotificationArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.SetBucketReplicationArgs;
import io.minio.SetBucketTagsArgs;
import io.minio.SetBucketVersioningArgs;
import io.minio.SetObjectLockConfigurationArgs;
import io.minio.SetObjectRetentionArgs;
import io.minio.SetObjectTagsArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.UploadObjectArgs;
import io.minio.UploadSnowballObjectsArgs;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.Item;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.NotificationConfiguration;
import io.minio.messages.NotificationRecords;
import io.minio.messages.ObjectLockConfiguration;
import io.minio.messages.ReplicationConfiguration;
import io.minio.messages.Retention;
import io.minio.messages.SseConfiguration;
import io.minio.messages.Tags;
import io.minio.messages.VersioningConfiguration;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.support.context.spring.SpringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero
 * @since 2021/10/20 13:36
 */
@Slf4j
public class MinioUtils {
    /* **************************************************** Delegate **************************************************** */
    @SneakyThrows
    public static StatObjectResponse statObject(StatObjectArgs args) {
        return getClient().statObject(args);
    }

    @SneakyThrows
    public static void deleteBucketLifecycle(DeleteBucketLifecycleArgs args) {
        getClient().deleteBucketLifecycle(args);
    }

    @SneakyThrows
    public static SelectResponseStream selectObjectContent(SelectObjectContentArgs args) {
        return getClient().selectObjectContent(args);
    }

    @SneakyThrows
    public static void disableDualStackEndpoint() {
        getClient().disableDualStackEndpoint();
    }

    @SneakyThrows
    public static void makeBucket(MakeBucketArgs args) {
        getClient().makeBucket(args);
    }

    @SneakyThrows
    public static void disableObjectLegalHold(DisableObjectLegalHoldArgs args) {
        getClient().disableObjectLegalHold(args);
    }

    @SneakyThrows
    public static void deleteObjectTags(DeleteObjectTagsArgs args) {
        getClient().deleteObjectTags(args);
    }

    @SneakyThrows
    public static String getPresignedObjectUrl(GetPresignedObjectUrlArgs args) {
        return getClient().getPresignedObjectUrl(args);
    }

    @SneakyThrows
    public static GetObjectResponse getObject(GetObjectArgs args) {
        return getClient().getObject(args);
    }

    @SneakyThrows
    public static void disableAccelerateEndpoint() {
        getClient().disableAccelerateEndpoint();
    }

    @SneakyThrows
    public static ObjectWriteResponse copyObject(CopyObjectArgs args) {
        return getClient().copyObject(args);
    }

    @SneakyThrows
    public static void traceOff() throws IOException {
        getClient().traceOff();
    }

    @SneakyThrows
    public static List<Bucket> listBuckets(ListBucketsArgs args) {
        return getClient().listBuckets(args);
    }

    @SneakyThrows
    public static Retention getObjectRetention(GetObjectRetentionArgs args) {
        return getClient().getObjectRetention(args);
    }

    @SneakyThrows
    public static void deleteBucketPolicy(DeleteBucketPolicyArgs args) {
        getClient().deleteBucketPolicy(args);
    }

    @SneakyThrows
    public static void deleteBucketReplication(DeleteBucketReplicationArgs args) {
        getClient().deleteBucketReplication(args);
    }

    @SneakyThrows
    public static Tags getObjectTags(GetObjectTagsArgs args) {
        return getClient().getObjectTags(args);
    }

    @SneakyThrows
    public static Iterable<Result<DeleteError>> removeObjects(RemoveObjectsArgs args) {
        return getClient().removeObjects(args);
    }

    @SneakyThrows
    public static ObjectLockConfiguration getObjectLockConfiguration(GetObjectLockConfigurationArgs args) {
        return getClient().getObjectLockConfiguration(args);
    }

    @SneakyThrows
    public static Iterable<Result<Item>> listObjects(ListObjectsArgs args) {
        return getClient().listObjects(args);
    }

    @SneakyThrows
    public static void setAppInfo(String name, String version) {
        getClient().setAppInfo(name, version);
    }

    @SneakyThrows
    public static String getBucketPolicy(GetBucketPolicyArgs args) {
        return getClient().getBucketPolicy(args);
    }

    @SneakyThrows
    public static ReplicationConfiguration getBucketReplication(GetBucketReplicationArgs args) {
        return getClient().getBucketReplication(args);
    }

    @SneakyThrows
    public static void setBucketTags(SetBucketTagsArgs args) {
        getClient().setBucketTags(args);
    }

    @SneakyThrows
    public static Map<String, String> getPresignedPostFormData(PostPolicy policy) {
        return getClient().getPresignedPostFormData(policy);
    }

    @SneakyThrows
    public static void setBucketVersioning(SetBucketVersioningArgs args) {
        getClient().setBucketVersioning(args);
    }

    @SneakyThrows
    public static void ignoreCertCheck() throws KeyManagementException, NoSuchAlgorithmException {
        getClient().ignoreCertCheck();
    }

    @SneakyThrows
    public static ObjectWriteResponse putObject(PutObjectArgs args) {
        return getClient().putObject(args);
    }

    @SneakyThrows
    public static void setBucketNotification(SetBucketNotificationArgs args) {
        getClient().setBucketNotification(args);
    }

    @SneakyThrows
    public static void setObjectLockConfiguration(SetObjectLockConfigurationArgs args) {
        getClient().setObjectLockConfiguration(args);
    }

    @SneakyThrows
    public static void deleteBucketEncryption(DeleteBucketEncryptionArgs args) {
        getClient().deleteBucketEncryption(args);
    }

    @SneakyThrows
    public static void enableObjectLegalHold(EnableObjectLegalHoldArgs args) {
        getClient().enableObjectLegalHold(args);
    }

    @SneakyThrows
    public static ObjectWriteResponse uploadSnowballObjects(UploadSnowballObjectsArgs args) {
        return getClient().uploadSnowballObjects(args);
    }

    @SneakyThrows
    public static void enableVirtualStyleEndpoint() {
        getClient().enableVirtualStyleEndpoint();
    }

    @SneakyThrows
    public static ObjectWriteResponse composeObject(ComposeObjectArgs args) {
        return getClient().composeObject(args);
    }

    @SneakyThrows
    public static boolean bucketExists(BucketExistsArgs args) {
        return getClient().bucketExists(args);
    }

    @SneakyThrows
    public static void setBucketLifecycle(SetBucketLifecycleArgs args) {
        getClient().setBucketLifecycle(args);
    }

    @SneakyThrows
    public static LifecycleConfiguration getBucketLifecycle(GetBucketLifecycleArgs args) {
        return getClient().getBucketLifecycle(args);
    }

    @SneakyThrows
    public static boolean isObjectLegalHoldEnabled(IsObjectLegalHoldEnabledArgs args) {
        return getClient().isObjectLegalHoldEnabled(args);
    }

    @SneakyThrows
    public static void setBucketEncryption(SetBucketEncryptionArgs args) {
        getClient().setBucketEncryption(args);
    }

    @SneakyThrows
    public static void enableDualStackEndpoint() {
        getClient().enableDualStackEndpoint();
    }

    @SneakyThrows
    public static void enableAccelerateEndpoint() {
        getClient().enableAccelerateEndpoint();
    }

    @SneakyThrows
    public static List<Bucket> listBuckets() {
        return getClient().listBuckets();
    }

    @SneakyThrows
    public static void downloadObject(DownloadObjectArgs args) {
        getClient().downloadObject(args);
    }

    @SneakyThrows
    public static void setBucketPolicy(SetBucketPolicyArgs args) {
        getClient().setBucketPolicy(args);
    }

    @SneakyThrows
    public static void setObjectRetention(SetObjectRetentionArgs args) {
        getClient().setObjectRetention(args);
    }

    @SneakyThrows
    public static void setBucketReplication(SetBucketReplicationArgs args) {
        getClient().setBucketReplication(args);
    }

    @SneakyThrows
    public static void setObjectTags(SetObjectTagsArgs args) {
        getClient().setObjectTags(args);
    }

    @SneakyThrows
    public static CloseableIterator<Result<NotificationRecords>> listenBucketNotification(ListenBucketNotificationArgs args) {
        return getClient().listenBucketNotification(args);
    }

    @SneakyThrows
    public static void deleteBucketTags(DeleteBucketTagsArgs args) {
        getClient().deleteBucketTags(args);
    }

    @SneakyThrows
    public static void deleteObjectLockConfiguration(DeleteObjectLockConfigurationArgs args) {
        getClient().deleteObjectLockConfiguration(args);
    }

    @SneakyThrows
    public static ObjectWriteResponse uploadObject(UploadObjectArgs args) {
        return getClient().uploadObject(args);
    }

    @SneakyThrows
    public static void deleteBucketNotification(DeleteBucketNotificationArgs args) {
        getClient().deleteBucketNotification(args);
    }

    @SneakyThrows
    public static Tags getBucketTags(GetBucketTagsArgs args) {
        return getClient().getBucketTags(args);
    }

    @SneakyThrows
    public static void traceOn(OutputStream traceStream) {
        getClient().traceOn(traceStream);
    }

    @SneakyThrows
    public static void restoreObject(RestoreObjectArgs args) {
        getClient().restoreObject(args);
    }

    @SneakyThrows
    public static VersioningConfiguration getBucketVersioning(GetBucketVersioningArgs args) {
        return getClient().getBucketVersioning(args);
    }

    @SneakyThrows
    public static void removeBucket(RemoveBucketArgs args) {
        getClient().removeBucket(args);
    }

    @SneakyThrows
    public static NotificationConfiguration getBucketNotification(GetBucketNotificationArgs args) {
        return getClient().getBucketNotification(args);
    }

    @SneakyThrows
    public static SseConfiguration getBucketEncryption(GetBucketEncryptionArgs args) {
        return getClient().getBucketEncryption(args);
    }

    @SneakyThrows
    public static void setTimeout(long connectTimeout, long writeTimeout, long readTimeout) {
        getClient().setTimeout(connectTimeout, writeTimeout, readTimeout);
    }

    @SneakyThrows
    public static void disableVirtualStyleEndpoint() {
        getClient().disableVirtualStyleEndpoint();
    }

    @SneakyThrows
    public static void removeObject(RemoveObjectArgs args) {
        getClient().removeObject(args);
    }

    /* **************************************************** Init **************************************************** */
    @Setter
    private static volatile MinioClient minioClient;

    public static MinioClient getClient() {
        checkAndCreate();
        return minioClient;
    }

    protected static void checkAndCreate() {
        if (Objects.isNull(minioClient)) {
            synchronized (MinioUtils.class) {
                if (Objects.isNull(minioClient)) {
                    minioClient = SpringUtils.getBean(MinioClient.class);
                }
            }
        }
    }

    protected MinioUtils() {
        throw new UnsupportedOperationException();
    }
}
