package fr.bramsou.messaging.netty.packet;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.handler.PacketHandler;

public interface NettyPacket {

    void serialize(PacketBuffer buffer);

    default void read(PacketHandler handler) {}

    default void write(PacketHandler handler) {}

    default boolean hasSendingPriority() {
        return true;
    }
}
