package org.zero.common.core.extension.spring.cloud.gateway.filter;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/16
 */
@FunctionalInterface
public interface LoadBalancer {
    ServiceInstance choose(List<ServiceInstance> serviceInstances);
}
