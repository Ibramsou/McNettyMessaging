package io.github.ibramsou.netty.messaging.api.event.session;

import io.github.ibramsou.netty.messaging.api.event.Event;
import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.session.Session;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Events related to session (connect and disconnect)
 */
@ParametersAreNonnullByDefault
public abstract class SessionEvent implements Event {

    private final Session session;
    private final Network network;

    public SessionEvent(Session session, Network network) {
        this.session = session;
        this.network = network;
    }

    /**
     * Get the current session
     * @return the instance of {@link Session}
     */
    public Session getSession() {
        return session;
    }

    /**
     * Get the current network
     * @return the instance of {@link Network}
     */
    public Network getNetwork() {
        return network;
    }
}
