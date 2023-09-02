package io.github.ibramsou.netty.messaging.api;

import io.github.ibramsou.netty.messaging.api.cipher.Encryption;
import io.github.ibramsou.netty.messaging.api.event.EventHandler;
import io.github.ibramsou.netty.messaging.api.network.NetworkRegistry;
import io.github.ibramsou.netty.messaging.api.pipeline.PipelineFactories;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.session.SessionType;

import javax.annotation.Nonnull;

public interface Messaging extends EventHandler {

    /**
     * Get the implementation of the API
     * @return an implementation of {@link Messaging}
     */
    static Messaging getInstance() {
        return MessagingApiService.getImplementation();
    }

    /**
     * Create a new session
     * @param type type of the session (client or server)
     * @return an instance of {@link Session}
     */
    Session createSession(@Nonnull SessionType type);

    /**
     * Get pipeline factories
     * @return the instance of {@link PipelineFactories}
     */
    PipelineFactories getPipelineFactories();

    /**
     * Get network states registry
     * @return the instance of {@link NetworkRegistry}
     */
    NetworkRegistry getRegistry();

    /**
     * Get network encryption message manager
     * @return the instance of {@link Encryption}
     */
    Encryption getEncryption();
}
