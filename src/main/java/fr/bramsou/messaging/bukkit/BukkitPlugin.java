package fr.bramsou.messaging.bukkit;

import fr.bramsou.messaging.netty.session.NettyClientSession;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {

    private NettyClientSession session;

    @Override
    public void onEnable() {
        this.session = new NettyClientSession().createConnection(27777);
    }
}
