package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyInitializer;
import fr.bramsou.messaging.netty.handler.PacketHandlerConstructor;
import fr.bramsou.messaging.netty.util.AddressResolver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.SocketAddress;

public class NettyServerSession extends NettySession {


    public NettyServerSession(PacketHandlerConstructor<?> constructor) {
        super(constructor);
    }

    public NettyServerSession bindConnection(int port) {
        final SocketAddress address = AddressResolver.resolveAddress(port);

        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(SERVER_SOCKET_CHANNEL_CLASS);
        bootstrap.childHandler(new NettyInitializer(this));
        bootstrap.group(this.getEventLoopGroup());
        bootstrap.localAddress(address);
        ChannelFuture future = bootstrap.bind();
        try {
            future.sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        return this;
    }
}
