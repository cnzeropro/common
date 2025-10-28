package org.zero.common.core.support.pingpong;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.zero.common.core.extension.java.lang.InnerBuilder;
import org.zero.common.core.extension.java.util.function.ThrowableSupplier;
import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@Log
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SocketPingClient implements PingClient {
    /**
     * 服务器地址
     */
    protected final InetAddress serverAddress;
    /**
     * 服务器端口
     */
    protected final int serverPort;
    /**
     * 本地地址。{@code null} 表示使用 {@linkplain InetAddress#anyLocalAddress() LocalAddress}
     */
    protected final InetAddress localAddress;
    /**
     * 本地端口。0 表示自动分配
     */
    protected final int localPort;
    /**
     * 超时时间，与此 {@code Socket} 关联的 {@code InputStream} 调用 {@linkplain InputStream#read() read} 方法的最大阻塞时间。单位：毫秒，为 0 时表示无限超时
     */
    protected final int timeout;
    /**
     * ping 间隔
     */
    protected final long period;
    /**
     * ping 间隔时间单位
     */
    protected final TimeUnit timeUnit;

    protected Thread thread;
    protected volatile boolean terminated = false;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void initialize() throws Exception {
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (terminated) {
                    return;
                }
                try {
                    @Cleanup Socket socket = new Socket();
                    socket.setSoTimeout(timeout);
                    InetSocketAddress localInetSocketAddress = new InetSocketAddress(localAddress, localPort);
                    socket.bind(localInetSocketAddress);
                    InetSocketAddress serverInetSocketAddress = new InetSocketAddress(serverAddress, serverPort);
                    socket.connect(serverInetSocketAddress, timeout);
                    @Cleanup InputStream inputStream = socket.getInputStream();
                    @Cleanup OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(PING);
                    outputStream.flush();
                    // 读取服务器返回的消息
                    int read = inputStream.read();
                    if (read == PongServer.PONG) {
                        if (log.isLoggable(Level.FINE)) {
                            log.log(Level.FINE, "server[{0}] pong success", serverInetSocketAddress);
                        }
                    } else if (read == -1) {
                        log.log(Level.WARNING, "server[{0}] closed", serverInetSocketAddress);
                    } else {
                        log.log(Level.WARNING, "server[{0}] pong error: {1}", new Object[]{serverInetSocketAddress, read});
                    }
                } catch (Exception e) {
                    log.log(Level.WARNING, "client process accepted socket failed!", e);
                }
                try {
                    timeUnit.sleep(period);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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
        thread.interrupt();
    }

    @Override
    public void destroy() throws Exception {
        this.close();
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends InnerBuilder<SocketPingClient, Builder> {
        /**
         * 服务器地址
         */
        protected InetAddress serverAddress = ThrowableUtil.ignore((ThrowableSupplier<InetAddress>) InetAddress::getLocalHost);
        /**
         * 服务器端口
         */
        protected int serverPort = PongServer.DEFAULT_PORT;
        /**
         * 本地地址。{@code null} 表示使用 {@linkplain InetAddress#anyLocalAddress() LocalAddress}
         */
        protected InetAddress localAddress;
        /**
         * 本地端口。0 表示自动分配
         */
        protected int localPort;
        /**
         * 超时时间，与此 {@code Socket} 关联的 {@code InputStream} 调用 {@linkplain InputStream#read() read} 方法的最大阻塞时间。单位：毫秒，为 0 时表示无限超时
         */
        protected int timeout;
        /**
         * ping 间隔
         */
        protected long period = 1000L;
        /**
         * ping 间隔时间单位
         */
        protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        @SneakyThrows
        public Builder serverHost(String serverHost) {
            InetAddress serverAddress = InetAddress.getByName(serverHost);
            return this.serverAddress(serverAddress);
        }

        @SneakyThrows
        public Builder localHost(String localHost) {
            InetAddress localAddress = InetAddress.getByName(localHost);
            return this.localAddress(localAddress);
        }
    }
}
