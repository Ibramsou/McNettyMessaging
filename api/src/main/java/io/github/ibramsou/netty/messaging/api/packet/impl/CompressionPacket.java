package io.github.ibramsou.netty.messaging.api.packet.impl;

import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CompressionPacket extends MessagingPacket {

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
    public void write(@Nonnull Network network) {
        network.setCompressionThreshold(network.getSession().config().get(MessagingOptions.COMPRESSION_THRESHOLD));
    }
}
