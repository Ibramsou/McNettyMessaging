package fr.bramsou.messaging.netty.packet.impl;

import fr.bramsou.messaging.netty.NettyNetwork;
import fr.bramsou.messaging.netty.NettyOptions;
import fr.bramsou.messaging.netty.handler.MessagingPacketListenerHandler;
import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.PacketBuffer;

import java.util.Objects;

public class CompressionPacket implements NettyPacket<MessagingPacketListenerHandler> {

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
    public void write(NettyNetwork network) {
        network.setCompressionThreshold(NettyOptions.COMPRESSION_THRESHOLD);
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
