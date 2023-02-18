package fr.bramsou.netty.messaging.registry;

import fr.bramsou.netty.messaging.packet.MessagingPacket;
import fr.bramsou.netty.messaging.packet.PacketBuffer;

@FunctionalInterface
public interface PacketFactory<T extends MessagingPacket<?>> {

    T construct(PacketBuffer buffer);
}
