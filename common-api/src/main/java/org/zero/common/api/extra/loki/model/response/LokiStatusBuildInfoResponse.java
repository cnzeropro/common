package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiStatusBuildInfoResponse implements Serializable {
    private String version;
    private String revision;
    private String branch;
    private String buildUser;
    private String buildDate;
    private String goVersion;
}
