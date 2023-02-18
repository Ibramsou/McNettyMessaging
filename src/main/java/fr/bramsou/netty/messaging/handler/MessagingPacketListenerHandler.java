package fr.bramsou.netty.messaging.handler;

import com.google.gson.JsonObject;
import fr.bramsou.netty.messaging.packet.impl.CompressionPacket;
import fr.bramsou.netty.messaging.packet.impl.TokenPacket;

public interface MessagingPacketListenerHandler extends PacketListenerHandler {

    default void handle(TokenPacket packet) {}

    default void handle(CompressionPacket packet) {}

    default void handleMessage(String message) {}

    default void handleJson(JsonObject object) {}
}
