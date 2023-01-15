package fr.bramsou.messaging.netty.packet;

public interface NettyPacket {

    void serialize(PacketBuffer buffer);

    default boolean hasSendingPriority() {
        return true;
    }
}
