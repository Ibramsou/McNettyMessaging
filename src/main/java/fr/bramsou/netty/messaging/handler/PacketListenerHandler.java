package fr.bramsou.netty.messaging.handler;

import fr.bramsou.netty.messaging.MessagingNetwork;

public interface PacketListenerHandler {

    MessagingNetwork getNetwork();
}
