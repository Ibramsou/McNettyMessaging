package fr.bramsou.messaging.netty.registry;

import fr.bramsou.messaging.netty.packet.NettyPacket;

public class PacketRegistryState {

    private final String name;

    public PacketRegistryState(String name) {
        this.name = name;
    }

    public PacketRegistryState register(int id, Class<? extends NettyPacket<?>> packet, PacketFactory<? extends NettyPacket<?>> constructor, PacketDirection... directions) {
        PacketRegistry.getInstance().register(this, id, packet, constructor, directions);
        return this;
    }

    public String getName() {
        return name;
    }
}
