package fr.bramsou.netty.messaging;

import fr.bramsou.netty.messaging.session.MessagingClientSession;
import fr.bramsou.netty.messaging.session.MessagingSession;
import io.netty.channel.*;

import java.net.InetSocketAddress;

public class MessagingInitializer extends ChannelInitializer<Channel> {

    private final MessagingSession session;

    public MessagingInitializer(MessagingSession session) {
        this.session = session;
    }

    @Override
    protected void initChannel(Channel channel) {
        InetSocketAddress address = (InetSocketAddress) channel.localAddress();
        if (this.session instanceof MessagingClientSession || this.session.getBuilder().isAuthorizeIncomingAddress() || address.getHostName().equals("127.0.0.1")) {
            this.session.getBuilder().getChannelOptions().forEach((option, value) -> channel.config().setOption(option, value));
            final ChannelPipeline pipeline = channel.pipeline();
            final MessagingNetwork network = new MessagingNetwork(this.session);
            this.session.getBuilder().getPipelineHandlers().stream().filter(entry -> !entry.afterNetwork)
                    .forEach(entry-> pipeline.addLast(entry.id, entry.constructor.construct(network)));
            pipeline.addLast("manager", network);
            this.session.getBuilder().getPipelineHandlers().stream().filter(entry -> entry.afterNetwork)
                    .forEach(entry -> pipeline.addLast(entry.id, entry.constructor.construct(network)));
        } else {
            channel.close();
        }
    }
}
