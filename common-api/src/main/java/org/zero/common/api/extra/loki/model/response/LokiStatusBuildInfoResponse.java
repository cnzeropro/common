package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Loki 构建信息响应。
 * <p>
 * 对应 {@code /loki/api/v1/status/buildinfo} 的响应体。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiStatusBuildInfoResponse implements Serializable {
    /**
     * {@code version} - Loki 版本号。
     */
    private String version;
    /**
     * {@code revision} - 构建对应的 Git revision。
     */
    private String revision;
    /**
     * {@code branch} - 构建使用的 Git 分支名。
     */
    private String branch;
    /**
     * {@code buildUser} - 构建用户。
     */
    private String buildUser;
    /**
     * {@code buildDate} - 构建时间。
     */
    private String buildDate;
    /**
     * {@code goVersion} - 构建时使用的 Go 版本。
     */
    private String goVersion;
}
