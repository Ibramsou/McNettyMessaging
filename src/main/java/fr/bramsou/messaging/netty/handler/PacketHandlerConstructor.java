package fr.bramsou.messaging.netty.handler;

import fr.bramsou.messaging.netty.NettyNetwork;

@FunctionalInterface
public interface PacketHandlerConstructor<T extends PacketHandler> {

    T construct(NettyNetwork network);
}
