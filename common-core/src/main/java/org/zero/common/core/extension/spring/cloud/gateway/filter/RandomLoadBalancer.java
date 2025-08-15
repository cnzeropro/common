package org.zero.common.core.extension.spring.cloud.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/16
 */
@RequiredArgsConstructor
public class RandomLoadBalancer implements LoadBalancer {
    protected final Random random;

    @SneakyThrows
    public RandomLoadBalancer() {
        this.random = SecureRandom.getInstanceStrong();
    }

    @Override
    public ServiceInstance choose(List<ServiceInstance> serviceInstances) {
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        int index = random.nextInt(serviceInstances.size());
        return serviceInstances.get(index);
    }
}
