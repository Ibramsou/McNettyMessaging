package fr.bramsou.netty.messaging.session;

import fr.bramsou.netty.messaging.MessagingBuilder;
import fr.bramsou.netty.messaging.MessagingInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

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
        final SocketAddress address = resolveAddress(host, port);

        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(this.getBuilder().getServerSocketChannel() == null ? SERVER_SOCKET_CHANNEL_CLASS : this.getBuilder().getServerSocketChannel());
        bootstrap.childHandler(new MessagingInitializer(this));
        this.configureBootstrap(bootstrap);
        bootstrap.localAddress(address);
        ChannelFuture future = bootstrap.bind();
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        this.configureConnection(future);
    }
}
