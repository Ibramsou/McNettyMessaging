package fr.bramsou.netty.messaging.handler;

import com.google.gson.JsonObject;
import fr.bramsou.netty.messaging.packet.impl.CompressionPacket;
import fr.bramsou.netty.messaging.packet.impl.JsonMessagePacket;
import fr.bramsou.netty.messaging.packet.impl.MessagePacket;
import fr.bramsou.netty.messaging.packet.impl.TokenPacket;

public interface MessagingPacketListenerHandler extends PacketListenerHandler {

    default void handle(TokenPacket packet) {}

    default void handle(CompressionPacket packet) {}

    default void handle(MessagePacket packet) {}

    default void handle(JsonMessagePacket packet) {}
}
