package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;

import java.net.SocketAddress;

public class NettyClientSession extends NettySession {

    public NettyClientSession(NettySessionListener listener) {
        super(listener);
    }

    public void createConnection(String host, int port) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(SOCKET_CHANNEL_CLASS);
        bootstrap.handler(new NettyInitializer(this));
        bootstrap.group(getEventLoopGroup());
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000);

        final SocketAddress address = resolveAddress(host, port);
        bootstrap.localAddress("0.0.0.0", 0);
        bootstrap.remoteAddress(address);
        bootstrap.connect();
    }
}
