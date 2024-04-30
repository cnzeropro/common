package org.zero.common.log.event;

import org.springframework.context.ApplicationEvent;
import org.zero.common.log.model.SysLog;

/**
 * 系统日志事件
 *
 * @author zero
 * @date 2022/1/5
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(SysLog source) {
        super(source);
    }

    @Override
    public SysLog getSource() {
        return (SysLog) source;
    }
}
