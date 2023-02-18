package fr.bramsou.netty.messaging.session;

import fr.bramsou.netty.messaging.MessagingInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.SocketAddress;

public class MessagingServerSession extends MessagingSession {


    public MessagingServerSession(MessagingSessionListener listener) {
        super(listener);
    }

    public void bindConnection(String host, int port) {
        final SocketAddress address = resolveAddress(host, port);

        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(SERVER_SOCKET_CHANNEL_CLASS);
        bootstrap.childHandler(new MessagingInitializer(this));
        bootstrap.group(getEventLoopGroup());
        bootstrap.localAddress(address);
        ChannelFuture future = bootstrap.bind();
        try {
            future.sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
