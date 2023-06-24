package server;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.event.session.SessionConnectEvent;
import io.github.ibramsou.netty.messaging.api.network.NetworkState;
import io.github.ibramsou.netty.messaging.api.packet.impl.MessagePacket;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.session.SessionConfig;
import io.github.ibramsou.netty.messaging.api.session.SessionType;
import io.netty.channel.ChannelOption;
import packet.TestPacket;

import java.util.concurrent.ThreadLocalRandom;

public class Server {

    public static void main(String[] args) {
        // Register custom packet to default network state
        NetworkState.DEFAULT_STATE.register(0x05, TestPacket.class, TestPacket::new);
        // Create a session server
        Session session = Messaging.getInstance().createSession(SessionType.SERVER);
        // Configure the session
        SessionConfig config = session.config();
        config.set(MessagingOptions.THROW_UNKNOWN_PACKET_ERRORS, true);
        config.set(MessagingOptions.HOST, "localhost");
        config.set(MessagingOptions.PORT, 4448);
        config.set(MessagingOptions.CHANNEL, ChannelOption.TCP_NODELAY, true);
        // Register listeners
        session.messaging().subscribe(SessionConnectEvent.class, event -> System.out.println("A client joined the server !"));
        session.messaging().subscribe(MessagePacket.class, event -> {
            if (event.getMessage().startsWith("Hi")) {
                event.getNetwork().sendPacket(new TestPacket(ThreadLocalRandom.current().nextInt(400)));
            }
        });
        // Open server connection
        session.connect();
    }
}
