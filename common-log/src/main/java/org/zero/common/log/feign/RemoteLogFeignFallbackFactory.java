package org.zero.common.log.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.zero.common.data.model.vo.Result;
import org.zero.common.log.model.SysLog;

/**
 * @author zero
 * @since 2021/7/13
 */
@Slf4j
@Component
public class RemoteLogFeignFallbackFactory implements FallbackFactory<RemoteLogService> {
    @Override
    public RemoteLogService create(Throwable cause) {
        log.warn("Remote Log Service is not available", cause);

        return new RemoteLogService() {
            @Override
            public Result<Boolean> save(SysLog sysLog) {
                return Result.fail();
            }
        };
    }
}