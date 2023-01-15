package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyNetwork;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public interface NettySession {

    EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup(0);

    NettyNetwork getNetwork();

    default void channelActive() {}
}
