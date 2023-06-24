package io.github.ibramsou.netty.messaging.core.session;

import io.github.ibramsou.netty.messaging.api.MessagingLoopGroup;
import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.session.SessionType;
import io.github.ibramsou.netty.messaging.core.MessagingInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

public class ClientSession extends AbstractSession {

    private int connectAttempts;
    private long lastConnectAttempt;

    @Override
    public void connect(InetSocketAddress address) {
        final Bootstrap bootstrap = new Bootstrap()
                .channel(MessagingLoopGroup.SOCKET_CHANNEL_CLASS)
                .group(this.config.get(MessagingOptions.LOOP_GROUP).getClientLoopGroup())
                .handler(new MessagingInitializer(this))
                .localAddress("0.0.0.0", 0)
                .remoteAddress(address);

        ChannelFuture result = this.openConnection(bootstrap, () -> {
            final ChannelFuture channelFuture = bootstrap.connect();
            channelFuture.addListener(future -> {
                if (!future.isSuccess()) {
                    boolean autoReconnect = this.config.get(MessagingOptions.AUTO_RECONNECT);
                    int connectionMaxAttempts = this.config.get(MessagingOptions.CONNECTION_MAX_ATTEMPTS);
                    int reconnectTime = this.config.get(MessagingOptions.AUTO_RECONNECT_TIME);
                    if (autoReconnect && (connectionMaxAttempts == -1 || connectAttempts <= connectionMaxAttempts)) {
                        long elapsed = System.currentTimeMillis() - this.lastConnectAttempt;
                        if (elapsed < reconnectTime) {
                            Thread.sleep(reconnectTime - elapsed);
                        }
                        this.connect(address);
                    }
                }
            });
            return channelFuture;
        });

        this.lastConnectAttempt = System.currentTimeMillis();
        this.connectAttempts++;

        this.synchronize(result);
    }

    @Override
    public SessionType getType() {
        return SessionType.CLIENT;
    }
}
