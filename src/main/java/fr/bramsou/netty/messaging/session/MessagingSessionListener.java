package fr.bramsou.netty.messaging.session;

import fr.bramsou.netty.messaging.MessagingNetwork;
import fr.bramsou.netty.messaging.handler.PacketListenerHandler;
import fr.bramsou.netty.messaging.packet.impl.CompressionPacket;
import fr.bramsou.netty.messaging.packet.impl.JsonMessagePacket;
import fr.bramsou.netty.messaging.packet.impl.MessagePacket;
import fr.bramsou.netty.messaging.packet.impl.TokenPacket;
import fr.bramsou.netty.messaging.registry.PacketRegistryState;
import fr.bramsou.netty.messaging.util.DisconnectReason;

public interface MessagingSessionListener {

    PacketRegistryState MESSAGING_STATE = new PacketRegistryState("DEFAULT_MESSAGING")
            .register(0x00, TokenPacket.class, TokenPacket::new)
            .register(0x01, CompressionPacket.class, CompressionPacket::new)
            .register(0x02, MessagePacket.class, MessagePacket::new)
            .register(0x03, JsonMessagePacket.class, JsonMessagePacket::new);

    PacketRegistryState getDefaultPacketState(MessagingNetwork network);

    PacketListenerHandler getDefaultPacketListener(MessagingNetwork network);

    void connected(MessagingNetwork network);

    void disconnected(MessagingNetwork network, String reason, Throwable cause);
}
