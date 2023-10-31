package io.github.ibramsou.netty.messaging.api.packet;

import io.github.ibramsou.netty.messaging.api.event.Event;
import io.github.ibramsou.netty.messaging.api.network.Network;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.annotation.Inherited;

@ParametersAreNonnullByDefault
public abstract class MessagingPacket implements Event {

    private Network network;
    private PacketDirection direction;

    /**
     * Serialize outgoing packet
     * @param buffer the instance of {@link PacketBuffer}
     */
    public abstract void serialize(PacketBuffer buffer);

    /**
     * Called when packet is sent
     * @param network connected network
     */
    public void write(Network network) {}

    /**
     * Get if packet has a sending priority
     * If false, sending will be posted in another thread
     * @return a boolean
     */
    public boolean hasSendingPriority() {
        return true;
    }

    /**
     * Get if packet should be read or just skipped
     * @return a boolean
     */
    public boolean shouldRead() {
        return true;
    }

    /**
     * Method is called when the packet is sent or received
     * @param network connected network
     */
    public final void setNetwork(@Nullable Network network) {
        this.network = network;
    }

    /**
     * Get connected network
     * @return the instance of {@link Network}
     */
    public final Network getNetwork() {
        return this.network;
    }

    /**
     * Get if packet is outgoing or incoming
     * @return a type of {@link PacketDirection}
     */
    public final PacketDirection getDirection() {
        return direction;
    }

    /**
     * Method is called when the packet is sent or received
     * @param direction a type of {@link PacketDirection}
     */
    public final void setDirection(PacketDirection direction) {
        this.direction = direction;
    }
}
