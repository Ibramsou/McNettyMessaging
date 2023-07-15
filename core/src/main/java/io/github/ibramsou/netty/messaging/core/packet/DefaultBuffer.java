package io.github.ibramsou.netty.messaging.core.packet;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;
import io.netty.buffer.ByteBuf;

class DefaultBuffer extends AbstractBuffer {

    public DefaultBuffer(ByteBuf buffer, Network network) {
        super(buffer, network);
    }

    @Override
    public PacketBuffer writeVarInt(int value) {
        while ((value & -128) != 0) {
            this.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        this.writeByte(value);
        return this;
    }
}
