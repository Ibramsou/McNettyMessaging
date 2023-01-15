package fr.bramsou.messaging.bungee;

import fr.bramsou.messaging.netty.session.NettyServerSession;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

    private NettyServerSession session;

    @Override
    public void onEnable() {
        this.session = new NettyServerSession().bindConnection(27777);
    }
}
