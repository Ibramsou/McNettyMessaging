package fr.bramsou.messaging.netty.packet;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.handler.PacketListenerHandler;

public interface NettyPacket<V extends PacketListenerHandler> {

    void serialize(PacketBuffer buffer);

    default void read(V handler) {}

    default void write(NettyNetwork network) {}

    default boolean hasSendingPriority() {
        return true;
    }
}
