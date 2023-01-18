package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.function.Supplier;

public abstract class NettySession {

    private static EventLoopGroup EVENT_LOOP_GROUP = null;
    protected static final Class<? extends ServerSocketChannel> SERVER_SOCKET_CHANNEL_CLASS;
    protected static final Class<? extends SocketChannel> SOCKET_CHANNEL_CLASS;
    protected static final Supplier<EventLoopGroup> EVENT_LOOP_GROUP_SUPPLIER;

    static {
        if (Epoll.isAvailable()) {
            SERVER_SOCKET_CHANNEL_CLASS = EpollServerSocketChannel.class;
            SOCKET_CHANNEL_CLASS = EpollSocketChannel.class;
            EVENT_LOOP_GROUP_SUPPLIER = () -> new EpollEventLoopGroup(0);
        } else {
            SERVER_SOCKET_CHANNEL_CLASS = NioServerSocketChannel.class;
            SOCKET_CHANNEL_CLASS = NioSocketChannel.class;
            EVENT_LOOP_GROUP_SUPPLIER = () -> new NioEventLoopGroup(0);
        }
    }

    protected final EventLoopGroup getEventLoopGroup() {
        if (EVENT_LOOP_GROUP == null) {
            EVENT_LOOP_GROUP = EVENT_LOOP_GROUP_SUPPLIER.get();
            return EVENT_LOOP_GROUP;
        }

        return EVENT_LOOP_GROUP.next();
    }

    public void connected(NettyNetwork network) {}

    public void disconnected(NettyNetwork network, DisconnectReason reason, Throwable cause) {
        if (reason == DisconnectReason.EXCEPTION_CAUGHT) {
            System.out.println("Disconnected for: " + cause.getMessage());
        } else {
            //TODO: Log
        }
    }
}
