package org.zero.common.core.support.pingpong;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.zero.common.core.extension.java.lang.BaseBuilder;
import org.zero.common.core.extension.java.util.function.ThrowThrowableSupplier;
import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@Log
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SocketPongServer implements PongServer {
    /**
     * 地址。{@code null} 表示使用 {@linkplain InetAddress#anyLocalAddress() LocalAddress}
     */
    protected final InetAddress inetAddress;
    /**
     * 端口号。0 表示自动分配
     */
    protected final int port;
    /**
     * 超时时间，与此 {@linkplain #serverSocket Socket} 关联的 {@code InputStream} 调用 {@linkplain InputStream#read() read} 方法的最大阻塞时间。单位：毫秒，为 0 时表示无限超时
     */
    protected final int timeout;
    /**
     * 连接队列的最大长度
     */
    protected final int backlog;
    /**
     * 是否允许立即重用处于 TIME_WAIT 状态的端口
     */
    protected final boolean reuseAddress;

    protected ServerSocket serverSocket;
    protected Thread thread;
    protected volatile boolean terminated = false;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void init() throws Exception {
        serverSocket = new ServerSocket();
        serverSocket.setSoTimeout(timeout);
        serverSocket.setReuseAddress(reuseAddress);
        serverSocket.bind(new InetSocketAddress(inetAddress, port), backlog);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (terminated) {
                    return;
                }
                try {
                    // 接收连接，如果没有连接，accept() 方法会阻塞
                    @Cleanup Socket socket = serverSocket.accept();
                    socket.setSoTimeout(timeout);
                    @Cleanup InputStream inputStream = socket.getInputStream();
                    @Cleanup OutputStream outputStream = socket.getOutputStream();
                    int read = inputStream.read();
                    if (read == PingClient.PING) {
                        if (log.isLoggable(Level.FINE)) {
                            log.log(Level.FINE, "client[{0}] ping success", socket);
                        }
                        outputStream.write(PONG);
                        outputStream.flush();
                    } else if (read == -1) {
                        log.log(Level.WARNING, "client[{0}] closed", socket);
                    } else {
                        if (log.isLoggable(Level.FINE)) {
                            log.log(Level.FINE, "client[{0}] ping error: {1}", new Object[]{socket, read});
                        }
                    }
                } catch (Exception e) {
                    log.log(Level.WARNING, "server process accepted socket failed!", e);
                }
            }
        },
                this.getClass().getSimpleName() + "-Thread");
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void close() throws IOException {
        terminated = true;
        serverSocket.close();
        thread.interrupt();
    }

    @Override
    public void destroy() throws Exception {
        this.close();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<SocketPongServer, Builder> {
        /**
         * 地址。{@code null} 表示使用 {@linkplain InetAddress#anyLocalAddress() LocalAddress}
         */
        protected InetAddress inetAddress = ThrowableUtil.ignore((ThrowThrowableSupplier<InetAddress>) InetAddress::getLocalHost);
        /**
         * 端口号。0 表示自动分配
         */
        protected int port = DEFAULT_PORT;
        /**
         * 超时时间，与此 {@linkplain #serverSocket Socket} 关联的 {@code InputStream} 调用 {@linkplain InputStream#read() read} 方法的最大阻塞时间。单位：毫秒，为 0 时表示无限超时
         */
        protected int timeout;
        /**
         * 连接队列的最大长度
         */
        protected int backlog = 50;
        /**
         * 是否允许立即重用处于 TIME_WAIT 状态的端口
         */
        protected boolean reuseAddress;

        @SneakyThrows
        public Builder host(String host) {
            InetAddress inetAddress = InetAddress.getByName(host);
            return this.inetAddress(inetAddress);
        }
    }
}
