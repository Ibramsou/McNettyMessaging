package io.github.ibramsou.netty.messaging.core.packet;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;
import io.netty.buffer.ByteBuf;

class OptimizedBuffer extends DefaultBuffer {

    public OptimizedBuffer(ByteBuf buffer, Network network) {
        super(buffer, network);
    }

    public PacketBuffer writeVarInt(int value) {
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            writeShort((value & 0x7F | 0x80) << 8 | (value >>> 7));
        } else {
            super.writeVarInt(value);
        }
        return this;
    }
}
