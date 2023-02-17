package fr.bramsou.messaging.netty.registry;

import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.PacketBuffer;

@FunctionalInterface
public interface PacketFactory<T extends NettyPacket<?>> {

    T construct(PacketBuffer buffer);
}
