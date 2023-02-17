package fr.bramsou.messaging.bungee;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.NettyOptions;
import fr.bramsou.messaging.netty.handler.MessagingPacketListenerHandler;
import fr.bramsou.messaging.netty.packet.impl.CompressionPacket;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class ServerPacketListener implements MessagingPacketListenerHandler {

    private final NettyNetwork network;

    private ServerInfo serverInfo;

    public ServerPacketListener(NettyNetwork network) {
        this.network = network;
    }

    @Override
    public void handle(TokenPacket packet) {
        final int port = packet.getPort();
        this.serverInfo = ProxyServer.getInstance().getServers().values().stream()
                .filter(server -> ((InetSocketAddress) server.getSocketAddress()).getPort() == port).findAny().orElse(null);
        if (this.serverInfo == null) {
            this.network.close(DisconnectReason.UNKNOWN_SERVER);
            return;
        }

        if (packet.getToken() != null && packet.getToken().equals("Password123")) {
            this.network.sendPacket(new CompressionPacket(NettyOptions.COMPRESSION_THRESHOLD));
            ProxyServer.getInstance().getLogger().info(serverInfo.getName() + " has connected to messaging system");
            return;
        }

        this.network.close(DisconnectReason.INCORRECT_TOKEN);
    }

    @Override
    public NettyNetwork getNetwork() {
        return this.network;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
