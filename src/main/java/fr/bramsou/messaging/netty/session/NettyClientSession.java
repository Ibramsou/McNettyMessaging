package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyInitializer;
import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.NettyOptions;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.util.AddressResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;

import java.net.SocketAddress;

public class NettyClientSession extends NettySession {

    public NettyClientSession createConnection(int port) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(SOCKET_CHANNEL_CLASS);
        bootstrap.handler(new NettyInitializer(this));
        bootstrap.group(this.getEventLoopGroup());
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000);

        final SocketAddress address = AddressResolver.resolveAddress(port);
        bootstrap.localAddress("0.0.0.0", 0);
        bootstrap.remoteAddress(address);
        bootstrap.connect();
        return this;
    }

    @Override
    public void connected(NettyNetwork network) {
        network.sendPacket(new TokenPacket(NettyOptions.VERIFY_TOKEN));
    }
}
