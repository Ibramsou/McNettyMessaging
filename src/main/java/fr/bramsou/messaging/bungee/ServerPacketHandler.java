package fr.bramsou.messaging.bungee;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.NettyOptions;
import fr.bramsou.messaging.netty.handler.PacketHandler;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ServerPacketHandler implements PacketHandler {

    private final NettyNetwork network;

    private ServerInfo info;

    public ServerPacketHandler(NettyNetwork network) {
        this.network = network;
    }

    @Override
    public void disconnected(DisconnectReason reason, Throwable cause) {
        final String message = reason == DisconnectReason.EXCEPTION_CAUGHT ? cause.getMessage() : reason.getMessage();
        final String serverName = this.info == null ? "Unkwnown" : this.info.getName();
        ProxyServer.getInstance().getLogger().info(String.format("%1s server disconnected for: %2s", serverName, message));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void read(TokenPacket packet) {
        final int port = packet.getPort();
        this.info = ProxyServer.getInstance().getServers().values().stream().filter(server -> server.getAddress().getPort() == port).findAny().orElse(null);
        if (this.info == null) {
            this.network.close(DisconnectReason.UNKNOWN_SERVER);
            return;
        }

        if (NettyOptions.VERIFY_TOKEN.equals(packet.getToken())) {
            ProxyServer.getInstance().getLogger().info(info.getName() + " has connected to messaging system");
            return;
        }

        this.network.close(DisconnectReason.INCORRECT_TOKEN);
    }
}
