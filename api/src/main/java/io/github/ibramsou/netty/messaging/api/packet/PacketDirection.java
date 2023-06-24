package io.github.ibramsou.netty.messaging.api.packet;

/**
 * Packet direction, sending or receiving
 * {@link #CLIENT_BOUND} = incoming packets
 * {@link #SERVER_BOUND} = outgouing packets
 */
public enum PacketDirection {
    CLIENT_BOUND,
    SERVER_BOUND
}
