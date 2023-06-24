package io.github.ibramsou.netty.messaging.api.pipeline;

import io.github.ibramsou.netty.messaging.api.network.Network;

@FunctionalInterface
public interface PipelineFactory {

    PipelineHandler construct(Network network);
}
