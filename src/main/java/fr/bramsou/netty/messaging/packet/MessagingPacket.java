package fr.bramsou.netty.messaging.packet;

import fr.bramsou.netty.messaging.MessagingNetwork;
import fr.bramsou.netty.messaging.handler.PacketListenerHandler;

public interface MessagingPacket<V extends PacketListenerHandler> {

    void serialize(PacketBuffer buffer);

    default void read(V handler) {}

    default void write(MessagingNetwork network) {}

    default boolean hasSendingPriority() {
        return true;
    }
}
