package fr.bramsou.messaging.bukkit;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.handler.MessagingPacketListenerHandler;
import fr.bramsou.messaging.netty.packet.impl.CompressionPacket;

public class BukkitPacketListener implements MessagingPacketListenerHandler {

    private final NettyNetwork network;

    public BukkitPacketListener(NettyNetwork network) {
        this.network = network;
    }

    @Override
    public void handle(CompressionPacket packet) {}

    @Override
    public NettyNetwork getNetwork() {
        return this.network;
    }
}
