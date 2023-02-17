package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;

import java.net.SocketAddress;

public class NettyClientSession extends NettySession {

    private boolean autoReconnect = false;
    private int maxConnectAttempts = -1; // -1 = infinite
    private int reconnectTime = 5_000; // milliseconds
    private int connectAttempts;
    private long lastConnectAttempt;

    public NettyClientSession(NettySessionListener listener) {
        super(listener);
    }

    public void createConnection(String host, int port) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(SOCKET_CHANNEL_CLASS);
        bootstrap.handler(new NettyInitializer(this));
        bootstrap.group(getEventLoopGroup());
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000);

        final SocketAddress address = resolveAddress(host, port);
        bootstrap.localAddress("0.0.0.0", 0);
        bootstrap.remoteAddress(address);
        bootstrap.connect().addListener(future -> {
            if (!future.isSuccess()) {
                if (this.autoReconnect && (this.maxConnectAttempts == -1 || this.connectAttempts <= this.maxConnectAttempts)) {
                    long elapsed = System.currentTimeMillis() - this.lastConnectAttempt;
                    if (elapsed < this.reconnectTime) {
                        Thread.sleep(this.reconnectTime - elapsed);
                    }
                    this.createConnection(host, port);
                }
            }
        });

        this.lastConnectAttempt = System.currentTimeMillis();
        this.connectAttempts++;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public int getMaxConnectAttempts() {
        return maxConnectAttempts;
    }

    public int getReconnectTime() {
        return reconnectTime;
    }

    public int getConnectAttempts() {
        return connectAttempts;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public void setMaxConnectAttempts(int maxConnectAttempts) {
        this.maxConnectAttempts = maxConnectAttempts;
    }

    public void setReconnectTime(int reconnectTime) {
        this.reconnectTime = reconnectTime;
    }
}
