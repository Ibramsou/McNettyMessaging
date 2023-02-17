package fr.bramsou.messaging.netty.session;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.handler.PacketListenerHandler;
import fr.bramsou.messaging.netty.packet.impl.CompressionPacket;
import fr.bramsou.messaging.netty.packet.impl.MessagePacket;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.registry.PacketRegistryState;
import fr.bramsou.messaging.netty.util.DisconnectReason;

public interface NettySessionListener {

    PacketRegistryState MESSAGING_STATE = new PacketRegistryState("DEFAULT_MESSAGING")
            .register(0x00, TokenPacket.class, TokenPacket::new)
            .register(0x01, CompressionPacket.class, CompressionPacket::new)
            .register(0x02, MessagePacket.class, MessagePacket::new);

    PacketRegistryState getDefaultPacketState(NettyNetwork network);

    PacketListenerHandler getDefaultPacketListener(NettyNetwork network);

    void connected(NettyNetwork network);

    void disconnected(NettyNetwork network, DisconnectReason reason, Throwable cause);
}
