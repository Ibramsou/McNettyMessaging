package fr.bramsou.messaging.bukkit;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.NettyOptions;
import fr.bramsou.messaging.netty.handler.PacketListenerHandler;
import fr.bramsou.messaging.netty.packet.impl.TokenPacket;
import fr.bramsou.messaging.netty.registry.PacketRegistryState;
import fr.bramsou.messaging.netty.session.NettyClientSession;
import fr.bramsou.messaging.netty.session.NettySessionListener;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

    private final NettySessionListener listener = new NettySessionListener() {
        @Override
        public PacketRegistryState getDefaultPacketState(NettyNetwork network) {
            return MESSAGING_STATE;
        }

        @Override
        public PacketListenerHandler getDefaultPacketListener(NettyNetwork network) {
            return new BukkitPacketListener(network);
        }

        @Override
        public void connected(NettyNetwork network) {
            network.sendPacket(new TokenPacket("Password123", Bukkit.getPort()));
        }

        @Override
        public void disconnected(NettyNetwork network, DisconnectReason reason, Throwable cause) {
            final String message = reason == DisconnectReason.EXCEPTION_CAUGHT ? cause.getMessage() : reason.getMessage();
            Bukkit.getLogger().info("Server closed for: " + message);
            cause.printStackTrace();
        }
    };

    @Override
    public void onEnable() {
        new NettyClientSession(this.listener).createConnection("localhost", 27777);
    }
}
