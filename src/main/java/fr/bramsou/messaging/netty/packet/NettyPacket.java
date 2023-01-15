package fr.bramsou.messaging.netty.packet;

import fr.bramsou.messaging.netty.NettyNetwork;

public interface NettyPacket {

    void serialize(PacketBuffer buffer);

    default void read(NettyNetwork network) {}

    default void write(NettyNetwork network) {}

    default boolean hasSendingPriority() {
        return true;
    }
}
