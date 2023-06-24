package io.github.ibramsou.netty.messaging.api;

import io.github.ibramsou.netty.messaging.api.cipher.Encryption;
import io.github.ibramsou.netty.messaging.api.event.EventHandler;
import io.github.ibramsou.netty.messaging.api.network.NetworkRegistry;
import io.github.ibramsou.netty.messaging.api.pipeline.PipelineFactories;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.session.SessionType;
import io.github.ibramsou.netty.messaging.api.util.Reflection;

import javax.annotation.Nonnull;

public abstract class Messaging extends EventHandler {

    private static final Messaging instance = Reflection.newInstance(
            Reflection.forName("io.github.ibramsou.netty.messaging.core.NettyMessaging").asSubclass(Messaging.class)
    );

    /**
     * Get the implementation of the API
     * @return an implementation of {@link Messaging}
     */
    public static Messaging getInstance() {
        return instance;
    }

    /**
     * Create a new session
     * @param type type of the session (client or server)
     * @return an instance of {@link Session}
     */
    public abstract Session createSession(@Nonnull SessionType type);

    /**
     * Get pipeline factories
     * @return the instance of {@link PipelineFactories}
     */
    public abstract PipelineFactories getPipelineFactories();

    /**
     * Get network states registry
     * @return the instance of {@link NetworkRegistry}
     */
    public abstract NetworkRegistry getRegistry();

    /**
     * Get network encryption message manager
     * @return the instance of {@link Encryption}
     */
    public abstract Encryption getEncryption();
}
