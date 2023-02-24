package fr.bramsou.netty.messaging.pipeline;

import fr.bramsou.netty.messaging.MessagingNetwork;
import io.netty.channel.ChannelHandler;

@FunctionalInterface
public interface PipelineFactory<T extends ChannelHandler> {

    T construct(MessagingNetwork network);
}
