package client;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.event.session.SessionConnectEvent;
import io.github.ibramsou.netty.messaging.api.packet.impl.MessagePacket;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.session.SessionConfig;
import io.github.ibramsou.netty.messaging.api.session.SessionType;
import io.netty.channel.ChannelOption;
import packet.TestCustomState;
import packet.TestPacket;

public class Client {

    public static void main(String[] args) {
        Messaging.getInstance().getRegistry().register("Example State");
        // Register custom packet to default network state
        //NetworkState.DEFAULT_STATE.register(0x05, TestPacket.class, TestPacket::new);
        // Create a session server
        Session session = Messaging.getInstance().createSession(SessionType.CLIENT);
        // Configure the session
        SessionConfig config = session.config();
        config.set(MessagingOptions.THROW_UNKNOWN_PACKET_ERRORS, true);
        config.set(MessagingOptions.HOST, "localhost");
        config.set(MessagingOptions.PORT, 25565);
        config.set(MessagingOptions.CHANNEL, ChannelOption.TCP_NODELAY, true);
        config.set(MessagingOptions.DEFAULT_NETWORK_STATE, TestCustomState.getCustomState());
        System.out.println("REGISTERED STATE: " + config.get(MessagingOptions.DEFAULT_NETWORK_STATE).getName());
        System.out.println("BOOLEAN: " + config.get(MessagingOptions.THROW_UNKNOWN_PACKET_ERRORS));
        // Register listeners
        session.messaging().subscribe(SessionConnectEvent.class, event -> event.getNetwork().sendPacket(new MessagePacket("Hi !")));
        session.messaging().subscribe(TestPacket.class, event -> System.out.println("Result: " + event.getRandomInteger()));
        // Open server connection
        session.connect();
    }
}
