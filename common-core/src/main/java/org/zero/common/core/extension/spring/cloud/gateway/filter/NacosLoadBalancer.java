package org.zero.common.core.extension.spring.cloud.gateway.filter;

import com.alibaba.cloud.nacos.balancer.NacosBalancer;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/16
 */
public class NacosLoadBalancer implements LoadBalancer {
    @Override
    public ServiceInstance choose(List<ServiceInstance> serviceInstances) {
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return null;
        }
        return NacosBalancer.getHostByRandomWeight3(serviceInstances);
    }
}
