package fr.bramsou.netty.messaging.session;

import fr.bramsou.netty.messaging.MessagingBuilder;
import fr.bramsou.netty.messaging.MessagingInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;

import java.net.SocketAddress;

public class MessagingClientSession extends MessagingSession {

    private boolean autoReconnect = false;
    private int maxConnectAttempts = -1; // -1 = infinite
    private int reconnectTime = 5_000; // milliseconds
    private int connectAttempts;
    private long lastConnectAttempt;

    public MessagingClientSession(MessagingSessionListener listener) {
        this(listener, MessagingBuilder.DEFAULT_BUILDER);
    }

    public MessagingClientSession(MessagingSessionListener listener, MessagingBuilder builder) {
        super(listener, builder);
    }

    public void createConnection(String host, int port) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(this.getBuilder().getSocketChannel() == null ? SOCKET_CHANNEL_CLASS : this.getBuilder().getSocketChannel());
        bootstrap.handler(new MessagingInitializer(this));
        this.configureBootstrap(bootstrap);
        final SocketAddress address = resolveAddress(host, port);
        bootstrap.localAddress("0.0.0.0", 0);
        bootstrap.remoteAddress(address);
        final ChannelFuture channelFuture = bootstrap.connect();
        channelFuture.addListener(future -> {
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
        this.configureConnection(channelFuture);

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
