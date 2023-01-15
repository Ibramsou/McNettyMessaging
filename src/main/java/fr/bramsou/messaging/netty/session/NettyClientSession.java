package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyInitializer;
import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.util.AddressResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;

public class NettyClientSession implements NettySession {

    private final NettyNetwork network = new NettyNetwork(this);

    public NettyClientSession createConnection(int port) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new NettyInitializer(this.network));
        bootstrap.group(EVENT_LOOP_GROUP.next());
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000);

        final SocketAddress address = AddressResolver.resolveAddress(port);
        bootstrap.localAddress("0.0.0.0", 0);
        bootstrap.remoteAddress(address);
        bootstrap.connect();

        return this;
    }


    @Override
    public NettyNetwork getNetwork() {
        return this.network;
    }

    @Override
    public void channelActive() {
        this.network.sendPacket(new TokenPacket("Hi !"));
    }
}
