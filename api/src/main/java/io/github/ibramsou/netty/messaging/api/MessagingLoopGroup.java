package io.github.ibramsou.netty.messaging.api;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.function.IntFunction;

public class MessagingLoopGroup {

    private static final IntFunction<EventLoopGroup> LOOP_GROUP_FUNCTION;
    public static final Class<? extends ServerSocketChannel> SERVER_SOCKET_CHANNEL_CLASS;
    public static final Class<? extends SocketChannel> SOCKET_CHANNEL_CLASS;
    public static MessagingLoopGroup DEFAULT_LOOP_GROUP = new MessagingLoopGroup(0);

    static {
        if (Epoll.isAvailable()) {
            SERVER_SOCKET_CHANNEL_CLASS = EpollServerSocketChannel.class;
            SOCKET_CHANNEL_CLASS = EpollSocketChannel.class;
            LOOP_GROUP_FUNCTION = EpollEventLoopGroup::new;
        } else {
            SERVER_SOCKET_CHANNEL_CLASS = NioServerSocketChannel.class;
            SOCKET_CHANNEL_CLASS = NioSocketChannel.class;
            LOOP_GROUP_FUNCTION = NioEventLoopGroup::new;
        }
    }

    private final int serverNettyThreads;
    private final int clientNettyThreads;
    private EventLoopGroup serverLoopGroup;
    private EventLoopGroup clientLoopGroup;

    public MessagingLoopGroup(int nettyThreads) {
        this(nettyThreads, nettyThreads);
    }

    public MessagingLoopGroup(int serverNettyThreads, int clientNettyThreads) {
        this.serverNettyThreads = serverNettyThreads;
        this.clientNettyThreads = clientNettyThreads;
    }

    public EventLoopGroup getServerLoopGroup() {
        if (this.serverLoopGroup == null) {
            return this.serverLoopGroup = this.createLoopGroup(this.serverNettyThreads);
        }

        return this.serverLoopGroup.next();
    }

    public EventLoopGroup getClientLoopGroup() {
        if (this.clientLoopGroup == null) {
            return this.clientLoopGroup = this.createLoopGroup(this.clientNettyThreads);
        }

        return this.clientLoopGroup.next();
    }

    private EventLoopGroup createLoopGroup(int nettyThreads) {
        return LOOP_GROUP_FUNCTION.apply(nettyThreads);
    }
}
