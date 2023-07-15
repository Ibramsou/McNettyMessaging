package io.github.ibramsou.netty.messaging.core.packet;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;
import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface PacketBufferFactory {

    PacketBufferFactory DEFAULT_FACTORY = DefaultBuffer::new;
    PacketBufferFactory OPTIMIZED_FACTORY = OptimizedBuffer::new;

    PacketBuffer construct(ByteBuf buffer, Network network);
}
