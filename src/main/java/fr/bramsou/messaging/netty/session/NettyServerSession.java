package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyInitializer;
import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.util.AddressResolver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.SocketAddress;

public class NettyServerSession implements NettySession {

    private final NettyNetwork network = new NettyNetwork(this);

    public NettyServerSession bindConnection(int port) {
        final SocketAddress address = AddressResolver.resolveAddress(port);

        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new NettyInitializer(this.network));
        bootstrap.group(EVENT_LOOP_GROUP.next());
        bootstrap.localAddress(address);
        bootstrap.bind().syncUninterruptibly();

        return this;
    }

    @Override
    public NettyNetwork getNetwork() {
        return this.network;
    }
}
