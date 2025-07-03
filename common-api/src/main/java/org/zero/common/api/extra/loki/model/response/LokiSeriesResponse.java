package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiSeriesResponse implements Serializable {
    private String chart;
    /**
     * controller_revision_hash
     */
    private String controllerRevisionHash;
    /**
     * statefulset_kubernetes_io_pod_name
     */
    private String statefulsetKubernetesIoPodName;
    /**
     * pod-template-hash
     */
    private String podTemplateHash;
    private String component;
    private String heritage;
    private String release;
    private String namespace;
    private String instance;
    /**
     * container_name
     */
    private String containerName;
    private String app;
    private String stream;
    private String filename;
    private String name;
    private String job;

}
