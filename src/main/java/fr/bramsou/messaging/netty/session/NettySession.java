package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public interface NettySession {

    EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup(0);

    NettyNetwork getNetwork();

    default void connected() {}

    default void disconnected(DisconnectReason reason, Throwable cause) {
        if (reason == DisconnectReason.EXCEPTION_CAUGHT) {
            throw new RuntimeException(reason.getMessage(), cause);
        } else {
            //TODO: Log
        }
    }
}
