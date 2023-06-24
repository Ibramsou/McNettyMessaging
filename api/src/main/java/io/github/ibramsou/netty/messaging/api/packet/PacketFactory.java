package io.github.ibramsou.netty.messaging.api.packet;

@FunctionalInterface
public interface PacketFactory<T extends MessagingPacket> {

    T construct(PacketBuffer buffer);
}
