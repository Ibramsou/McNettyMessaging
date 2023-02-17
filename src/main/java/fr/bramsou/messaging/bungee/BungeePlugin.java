package fr.bramsou.messaging.bungee;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.handler.PacketListenerHandler;
import fr.bramsou.messaging.netty.registry.PacketRegistryState;
import fr.bramsou.messaging.netty.session.NettyServerSession;
import fr.bramsou.messaging.netty.session.NettySessionListener;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

    private final NettySessionListener listener = new NettySessionListener() {

        @Override
        public PacketRegistryState getDefaultPacketState(NettyNetwork network) {
            return MESSAGING_STATE;
        }

        @Override
        public PacketListenerHandler getDefaultPacketListener(NettyNetwork network) {
            return new ServerPacketListener(network);
        }

        @Override
        public void connected(NettyNetwork network) {}

        @Override
        public void disconnected(NettyNetwork network, DisconnectReason reason, Throwable cause) {
            final String message = reason == DisconnectReason.EXCEPTION_CAUGHT ? cause.getMessage() : reason.getMessage();
            final ServerPacketListener handler = (ServerPacketListener) network.getPacketListener().orElseThrow(RuntimeException::new);
            final ServerInfo info = handler.getServerInfo();
            final String serverName = info == null ? "Unkwnown" : info.getName();
            ProxyServer.getInstance().getLogger().info(String.format("%1s server disconnected for: %2s", serverName, message));
        }
    };

    @Override
    public void onEnable() {
        new NettyServerSession(this.listener).bindConnection("localhost", 27777);
    }
}
