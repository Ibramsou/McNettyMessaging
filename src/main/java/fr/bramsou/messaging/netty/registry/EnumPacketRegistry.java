package fr.bramsou.messaging.netty.registry;

import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.impl.CompressionPacket;
import fr.bramsou.messaging.netty.packet.impl.MessagePacket;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;

import java.util.HashMap;
import java.util.Map;

public enum EnumPacketRegistry {
    TOKEN_PACKET(0x00, TokenPacket.class, TokenPacket::new),
    COMPRESSION_PACKET(0x01, CompressionPacket.class, CompressionPacket::new),
    MESSAGE_PACKET(0x02, MessagePacket.class, MessagePacket::new);

    public static final Map<Integer, PacketConstructor<? extends NettyPacket>> DECODER_PACKET = new HashMap<>();
    public static final  Map<Class<? extends NettyPacket>, Integer> ENCODER_PACKETS = new HashMap<>();

    static {
        for (EnumPacketRegistry value : values()) {
            value.register();
        }
    }

    private final int packetId;
    private final Class<? extends NettyPacket> packetClass;
    private final PacketConstructor<? extends NettyPacket> constructor;
    private final PacketDirection[] directions;

    EnumPacketRegistry(int packetId, Class<? extends NettyPacket> packetClass, PacketConstructor<? extends NettyPacket> constructor, PacketDirection... directions) {
        this.packetId = packetId;
        this.packetClass = packetClass;
        this.constructor = constructor;
        if (directions == null || directions.length == 0) {
            this.directions = new PacketDirection[] {PacketDirection.CLIENT_BOUND, PacketDirection.SERVER_BOUND};
        } else {
            this.directions = directions;
        }
    }

    private void register() {
        boolean client = false;
        boolean server = false;

        for (PacketDirection direction : directions) {
            if (direction == PacketDirection.CLIENT_BOUND) client = true;
            if (direction == PacketDirection.SERVER_BOUND) server = true;
        }

        if (client) {
            DECODER_PACKET.put(this.packetId, this.constructor);
        }
        if (server) {
            ENCODER_PACKETS.put(this.packetClass, this.packetId);
        }
    }

}
