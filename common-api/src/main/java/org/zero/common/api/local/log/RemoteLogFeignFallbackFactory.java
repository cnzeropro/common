package org.zero.common.api.local.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.zero.common.api.local.log.model.SysLogPO;
import org.zero.common.data.model.vo.Result;

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
            public Result<Boolean> save(SysLogPO sysLog) {
                return Result.fail();
            }
        };
    }
}