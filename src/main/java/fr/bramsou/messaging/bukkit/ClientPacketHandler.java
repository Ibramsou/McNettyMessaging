package fr.bramsou.messaging.bukkit;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.NettyOptions;
import fr.bramsou.messaging.netty.handler.PacketHandler;
import fr.bramsou.messaging.netty.packet.impl.CompressionPacket;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import org.bukkit.Bukkit;

public class ClientPacketHandler implements PacketHandler {

    private final NettyNetwork network;

    public ClientPacketHandler(NettyNetwork network) {
        this.network = network;
    }

    @Override
    public void connected() {
        this.network.sendPacket(new TokenPacket(NettyOptions.VERIFY_TOKEN, Bukkit.getPort()));
    }

    @Override
    public void disconnected(DisconnectReason reason, Throwable cause) {
        final String message = reason == DisconnectReason.EXCEPTION_CAUGHT ? cause.getMessage() : reason.getMessage();
        Bukkit.getLogger().info("Server closed for: " + message);
    }

    @Override
    public void read(CompressionPacket packet) {
        this.network.setCompressionThreshold(packet.getThreshold());
    }
}
