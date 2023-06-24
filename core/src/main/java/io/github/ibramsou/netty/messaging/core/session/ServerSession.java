package io.github.ibramsou.netty.messaging.core.session;

import io.github.ibramsou.netty.messaging.api.MessagingLoopGroup;
import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.session.SessionType;
import io.github.ibramsou.netty.messaging.core.MessagingInitializer;
import io.netty.bootstrap.ServerBootstrap;

import java.net.InetSocketAddress;

public class ServerSession extends AbstractSession {
    @Override
    public void connect(InetSocketAddress address) {
        final ServerBootstrap bootstrap = new ServerBootstrap()
                .channel(MessagingLoopGroup.SERVER_SOCKET_CHANNEL_CLASS)
                .group(this.config.get(MessagingOptions.LOOP_GROUP).getServerLoopGroup())
                .childHandler(new MessagingInitializer(this))
                .localAddress(address);
        this.synchronize(this.openConnection(bootstrap, bootstrap::bind));
    }

    @Override
    public SessionType getType() {
        return SessionType.SERVER;
    }
}
