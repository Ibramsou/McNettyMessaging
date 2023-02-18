package fr.bramsou.netty.messaging.registry;

import fr.bramsou.netty.messaging.packet.MessagingPacket;

public class PacketRegistryState {

    private final String name;

    public PacketRegistryState(String name) {
        this.name = name;
    }

    public PacketRegistryState register(int id, Class<? extends MessagingPacket<?>> packet, PacketFactory<? extends MessagingPacket<?>> constructor, PacketDirection... directions) {
        PacketRegistry.getInstance().register(this, id, packet, constructor, directions);
        return this;
    }

    public String getName() {
        return name;
    }
}
