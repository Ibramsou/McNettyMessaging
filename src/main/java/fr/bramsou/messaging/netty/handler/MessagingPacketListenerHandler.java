package fr.bramsou.messaging.netty.handler;

import com.google.gson.JsonObject;
import fr.bramsou.messaging.netty.packet.impl.CompressionPacket;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;

public interface MessagingPacketListenerHandler extends PacketListenerHandler {

    default void handle(TokenPacket packet) {}

    default void handle(CompressionPacket packet) {}

    default void handleMessage(String message) {}

    default void handleJson(JsonObject object) {}
}
