package io.github.ibramsou.netty.messaging.core;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.session.SessionConfig;
import io.github.ibramsou.netty.messaging.core.network.MessagingNetwork;
import io.github.ibramsou.netty.messaging.core.session.AbstractSession;
import io.github.ibramsou.netty.messaging.core.session.ClientSession;
import io.netty.channel.*;

import java.net.InetSocketAddress;

public class MessagingInitializer extends ChannelInitializer<Channel> {

    private final Messaging messaging;
    private final AbstractSession session;

    public MessagingInitializer(AbstractSession session) {
        this.session = session;
        this.messaging = Messaging.getInstance();
    }

    @Override
    @SuppressWarnings("all")
    protected void initChannel(Channel channel) {
        final InetSocketAddress address = (InetSocketAddress) channel.localAddress();
        final SessionConfig config = session.config();
        if (this.session instanceof ClientSession || config.get(MessagingOptions.AUTHORIZE_INCOMING_ADDRESSES) || address.getHostName().equals("127.0.0.1")) {
            config.get(MessagingOptions.CHANNEL).forEach((channelOption, o) -> channel.config().setOption((ChannelOption) channelOption, o));
            final Network network = new MessagingNetwork(this.messaging, this.session);
            ChannelPipeline pipeline = channel.pipeline();
            config.get(MessagingOptions.BEFORE_PIPELINE_HANDLERS).forEach((id, factory) -> pipeline.addLast(id, factory.construct(network)));
            pipeline.addLast("manager", network.handle());
            config.get(MessagingOptions.AFTER_PIPELINE_HANDLERS).forEach((id, factory) -> pipeline.addLast(id, factory.construct(network)));
        } else {
            channel.close();
        }
    }
}
