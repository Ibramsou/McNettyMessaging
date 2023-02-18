package fr.bramsou.netty.messaging.packet.impl;

import fr.bramsou.netty.messaging.MessagingNetwork;
import fr.bramsou.netty.messaging.MessagingOptions;
import fr.bramsou.netty.messaging.handler.MessagingPacketListenerHandler;
import fr.bramsou.netty.messaging.packet.MessagingPacket;
import fr.bramsou.netty.messaging.packet.PacketBuffer;

import java.util.Objects;

public class CompressionPacket implements MessagingPacket<MessagingPacketListenerHandler> {

    private final int threshold;

    public CompressionPacket(int threshold) {
        this.threshold = threshold;
    }

    public CompressionPacket(PacketBuffer buffer) {
        this.threshold = buffer.readVarInt();
    }

    @Override
    public void serialize(PacketBuffer buffer) {
        buffer.writeVarInt(this.threshold);
    }

    @Override
    public void read(MessagingPacketListenerHandler handler) {
        handler.getNetwork().setCompressionThreshold(this.threshold);
        handler.handle(this);
    }

    @Override
    public void write(MessagingNetwork network) {
        network.setCompressionThreshold(MessagingOptions.COMPRESSION_THRESHOLD);
    }

    public int getThreshold() {
        return threshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompressionPacket that = (CompressionPacket) o;
        return threshold == that.threshold;
    }

    @Override
    public int hashCode() {
        return Objects.hash(threshold);
    }

    @Override
    public String toString() {
        return "CompressionPacket{" +
                "threshold=" + threshold +
                '}';
    }
}
