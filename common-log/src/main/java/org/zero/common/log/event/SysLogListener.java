package org.zero.common.log.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.zero.common.api.local.log.RemoteLogService;
import org.zero.common.api.local.log.model.SysLogPO;

/**
 * 异步监听日志事件
 */
@Slf4j
@RequiredArgsConstructor
public class SysLogListener {
	private final RemoteLogService remoteLogService;

	// @Async
	@EventListener(SysLogEvent.class)
	public void saveSysLog(SysLogEvent event) {
		SysLogPO sysLog = event.getSource();
		remoteLogService.save(sysLog);
	}
}
