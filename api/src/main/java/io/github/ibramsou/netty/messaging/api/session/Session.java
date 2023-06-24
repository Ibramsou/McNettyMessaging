package io.github.ibramsou.netty.messaging.api.session;

import io.github.ibramsou.netty.messaging.api.Messaging;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Session {

    /**
     * Configuration of the session, using default value of {@link io.github.ibramsou.netty.messaging.api.MessagingOptions} if none
     * @return the instance of {@link Session}
     */
    SessionConfig config();

    /**
     * Connect the session
     */
    void connect();

    /**
     * Get the type of the session
     * @return a type of {@link SessionType}
     */
    SessionType getType();

    /**
     * The host of the session
     * @return a string address
     */
    String getHost();

    /**
     * The port of the session
     * @return an integer port address
     */
    int getPort();

    default Messaging messaging() {
        return Messaging.getInstance();
    }

}
