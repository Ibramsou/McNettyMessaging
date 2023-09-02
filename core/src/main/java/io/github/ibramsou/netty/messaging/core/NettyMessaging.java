package io.github.ibramsou.netty.messaging.core;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.cipher.Encryption;
import io.github.ibramsou.netty.messaging.api.network.NetworkRegistry;
import io.github.ibramsou.netty.messaging.api.pipeline.PipelineFactories;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.session.SessionType;
import io.github.ibramsou.netty.messaging.core.cipher.MessagingEncryption;
import io.github.ibramsou.netty.messaging.core.network.MessagingNetworkRegistry;
import io.github.ibramsou.netty.messaging.core.pipeline.MessagingPipelineFactories;
import io.github.ibramsou.netty.messaging.core.session.ClientSession;
import io.github.ibramsou.netty.messaging.core.session.ServerSession;

import javax.annotation.Nonnull;

public class NettyMessaging extends MessagingEventHandler implements Messaging {

    private final MessagingNetworkRegistry registry = new MessagingNetworkRegistry();
    private final MessagingPipelineFactories factories = new MessagingPipelineFactories();
    private final MessagingEncryption encryption = new MessagingEncryption();

    @Override
    public Session createSession(@Nonnull SessionType type) {
        return type == SessionType.CLIENT ? new ClientSession() : new ServerSession();
    }

    @Override
    public PipelineFactories getPipelineFactories() {
        return this.factories;
    }

    @Override
    public NetworkRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public Encryption getEncryption() {
        return this.encryption;
    }
}
