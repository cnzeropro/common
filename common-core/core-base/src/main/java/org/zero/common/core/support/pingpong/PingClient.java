package org.zero.common.core.support.pingpong;

import org.zero.common.core.extension.spring.beans.factory.LifeCycleBean;

import java.io.Closeable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
public interface PingClient extends Closeable, LifeCycleBean {
    byte PING = 0;

    void start();
}
