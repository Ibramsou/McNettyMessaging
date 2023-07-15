package io.github.ibramsou.netty.messaging.api.pipeline;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.netty.channel.ChannelHandler;

@FunctionalInterface
public interface PipelineFactory {

    ChannelHandler construct(Network network);
}
