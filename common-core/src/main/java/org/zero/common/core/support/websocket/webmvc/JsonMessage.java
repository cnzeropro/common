package org.zero.common.core.support.websocket.webmvc;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
@Data
public class JsonMessage implements Serializable {
    /**
     * 消息类型
     * <p>
     * 用于分发到对应的 {@link WebSocketMessageListener} 实现类
     */
    private String type;
    /**
     * 消息内容
     */
    private String content;
}
