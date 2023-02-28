package fr.bramsou.netty.messaging.session;

import fr.bramsou.netty.messaging.MessagingBuilder;
import fr.bramsou.netty.messaging.MessagingInitializer;
import fr.bramsou.netty.messaging.MessagingLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.SocketAddress;

public class MessagingServerSession extends MessagingSession {

    public MessagingServerSession(MessagingSessionListener listener) {
        this(listener, MessagingBuilder.DEFAULT_BUILDER);
    }

    public MessagingServerSession(MessagingSessionListener listener, MessagingBuilder builder) {
        super(listener, builder);
    }

    @Override
    public void disconnect() {}

    public void bindConnection(String host, int port) {
        final SocketAddress address = this.resolveAddress(host, port);

        final ServerBootstrap bootstrap = new ServerBootstrap()
                .channel(MessagingLoopGroup.SERVER_SOCKET_CHANNEL_CLASS)
                .group(this.getBuilder().getLoopGroup().getServerLoopGroup())
                .childHandler(new MessagingInitializer(this))
                .localAddress(address);

        this.configureBootstrap(bootstrap);
        final ChannelFuture future = bootstrap.bind();
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        this.configureConnection(future);
    }
}
