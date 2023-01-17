package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyInitializer;
import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.NettyOptions;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.util.AddressResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

import java.net.SocketAddress;

public class NettyClientSession extends NettySession {

    private final NettyNetwork network = new NettyNetwork(this);


    public NettyClientSession createConnection(int port) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(SOCKET_CHANNEL_CLASS);
        bootstrap.handler(new NettyInitializer(this.network));
        bootstrap.group(this.getEventLoopGroup());
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000);

        final SocketAddress address = AddressResolver.resolveAddress(port);
        bootstrap.localAddress("0.0.0.0", 0);
        bootstrap.remoteAddress(address);
        final ChannelFuture future = bootstrap.connect();
        try {
            future.sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        return this;
    }


    @Override
    public NettyNetwork getNetwork() {
        return this.network;
    }

    @Override
    public void connected() {
        this.network.sendPacket(new TokenPacket(NettyOptions.VERIFY_TOKEN));
    }
}
