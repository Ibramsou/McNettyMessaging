package io.github.ibramsou.netty.messaging.api.event.session;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.util.DisconnectReason;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

/**
 * Event called while a session disconnect from the server
 */
@ParametersAreNullableByDefault
public class SessionDisconnectEvent extends SessionEvent {

    private final DisconnectReason reason;
    private final String message;
    private final Throwable cause;

    public SessionDisconnectEvent(@Nonnull Session session, @Nonnull Network network, DisconnectReason reason, String message, Throwable cause) {
        super(session, network);

        this.message = message;
        this.reason = reason;
        this.cause = cause;
    }

    /**
     * Get the reason of the disconnection
     * @return a type of {@link DisconnectReason} if not null
     */
    public DisconnectReason getReason() {
        return reason;
    }

    /**
     * Get the message of the disconnection
     * @return a String if not null
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the potential error thrown while session got disconnected
     * @return an instance of {@link Throwable}
     */
    public Throwable getCause() {
        return cause;
    }
}
