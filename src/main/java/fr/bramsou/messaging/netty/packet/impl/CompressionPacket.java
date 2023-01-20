package fr.bramsou.messaging.netty.packet.impl;

import fr.bramsou.messaging.netty.handler.PacketHandler;
import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.PacketBuffer;

import java.util.Objects;

public class CompressionPacket implements NettyPacket {

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
    public void read(PacketHandler handler) {
        handler.read(this);
    }

    @Override
    public void write(PacketHandler handler) {
        handler.write(this);
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
