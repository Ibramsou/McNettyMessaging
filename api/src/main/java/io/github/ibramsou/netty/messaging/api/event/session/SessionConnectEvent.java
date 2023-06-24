package io.github.ibramsou.netty.messaging.api.event.session;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.session.Session;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Event called while session is connected to a server
 * Or while a client connect to the server
 */
@ParametersAreNonnullByDefault
public class SessionConnectEvent extends SessionEvent {

    public SessionConnectEvent(Session session, Network network) {
        super(session, network);
    }
}
