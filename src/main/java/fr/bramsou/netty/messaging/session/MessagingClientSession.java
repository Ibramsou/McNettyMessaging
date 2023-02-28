package fr.bramsou.netty.messaging.session;

import fr.bramsou.netty.messaging.MessagingBuilder;
import fr.bramsou.netty.messaging.MessagingInitializer;
import fr.bramsou.netty.messaging.MessagingLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.net.SocketAddress;
import java.util.concurrent.*;

public class MessagingClientSession extends MessagingSession {

    private static final ScheduledExecutorService RECONNECT_THREAD = Executors.newSingleThreadScheduledExecutor();

    private boolean autoReconnect = false;
    private boolean reconnectOnKick = false;
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

    @Override
    public void disconnect() {
        if (this.autoReconnect && this.reconnectOnKick) {
            RECONNECT_THREAD.schedule(() -> this.createConnection(this.getHost(), this.getPort()), this.reconnectTime, TimeUnit.MILLISECONDS);
        }
    }

    public void createConnection(String host, int port) {
        final SocketAddress address = this.resolveAddress(host, port);

        final Bootstrap bootstrap = new Bootstrap()
                .channel(MessagingLoopGroup.SOCKET_CHANNEL_CLASS)
                .group(this.getBuilder().getLoopGroup().getClientLoopGroup())
                .handler(new MessagingInitializer(this))
                .localAddress("0.0.0.0", 0)
                .remoteAddress(address);

        this.configureBootstrap(bootstrap);
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
        this.setHost(host);
        this.setPort(port);

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

    public boolean isReconnectOnKick() {
        return reconnectOnKick;
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

    public void setReconnectOnKick(boolean reconnectOnKick) {
        this.reconnectOnKick = reconnectOnKick;
    }
}
