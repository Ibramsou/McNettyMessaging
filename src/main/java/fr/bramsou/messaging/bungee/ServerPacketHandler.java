package fr.bramsou.messaging.bungee;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.NettyOptions;
import fr.bramsou.messaging.netty.handler.PacketHandler;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import net.md_5.bungee.api.ProxyServer;

public class ServerPacketHandler implements PacketHandler {

    private final NettyNetwork network;

    public ServerPacketHandler(NettyNetwork network) {
        this.network = network;
    }

    @Override
    public void disconnected(DisconnectReason reason, Throwable cause) {
        final String message = reason == DisconnectReason.EXCEPTION_CAUGHT ? cause.getMessage() : reason.getMessage();
        ProxyServer.getInstance().getLogger().info("Server disconnected for: " + message);
    }

    @Override
    public void read(TokenPacket packet) {
        if (NettyOptions.VERIFY_TOKEN.equals(packet.getToken())) {
            System.out.println("Token: " + packet.getToken());
        }
    }
}
