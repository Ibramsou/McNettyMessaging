package fr.bramsou.messaging.netty.handler;

import fr.bramsou.messaging.netty.NettyNetwork;

public interface PacketListenerHandler {

    NettyNetwork getNetwork();
}
