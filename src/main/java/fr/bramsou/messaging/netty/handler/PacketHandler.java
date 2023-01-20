package fr.bramsou.messaging.netty.handler;

import fr.bramsou.messaging.netty.packet.impl.CompressionPacket;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.util.DisconnectReason;

public interface PacketHandler {

    default void connected() {}

    default void disconnected(DisconnectReason reason, Throwable cause) {}

    default void read(TokenPacket packet) {}

    default void read(CompressionPacket packet) {}

    default void write(CompressionPacket packet) {}
}
