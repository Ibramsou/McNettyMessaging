package io.github.ibramsou.netty.messaging.core.packet;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.StandardCharsets;

abstract class AbstractBuffer implements PacketBuffer {

    private final ByteBuf buffer;
    private final Network network;

    protected AbstractBuffer(ByteBuf buffer, Network network) {
        this.buffer = buffer;
        this.network = network;
    }

    @Override
    public Network network() {
        return this.network;
    }

    @Override
    public Session session() {
        return this.network.getSession();
    }

    @Override
    public ByteBuf buffer() {
        return this.buffer;
    }

    @Override
    public ByteBuf writeBytes(byte[] bytes) {
        return this.buffer.writeBytes(bytes);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf) {
        return this.buffer.writeBytes(buf);
    }

    @Override
    public ByteBuf readBytes(byte[] array) {
        return this.buffer.readBytes(array);
    }

    @Override
    public PacketBuffer writeString(String value) {
        byte[] array = value.getBytes(StandardCharsets.UTF_8);
        if (array.length > Short.MAX_VALUE) {
            throw new EncoderException("String too big (was " + value.length() + " bytes encoded, max " + Short.MAX_VALUE + ")");
        } else {
            this.writeVarInt(array.length);
            this.writeBytes(array);
        }

        return this;
    }

    @Override
    public String readString() {
        return readString(Short.MAX_VALUE);
    }

    @Override
    public String readString(int capacity) {
        int length = this.readVarInt();
        if (length > capacity * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + length + " > " + capacity * 4 + ")");
        } else if (length < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            byte[] array = new byte[length];
            this.readBytes(array);
            String result = new String(array);
            if (result.length() > capacity) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + length + " > " + capacity + ")");
            }

            return result;
        }
    }

    @Override
    public PacketBuffer writeByteArray(byte[] value) {
        this.writeVarInt(value.length);
        this.writeBytes(value);
        return this;
    }

    @Override
    public byte[] readByteArray() {
        byte[] array = new byte[this.readVarInt()];
        this.readBytes(array);
        return array;
    }

    @Override
    public int readVarInt() {
        int i = 0;
        int j = 0;

        byte b0;

        do {
            b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    @Override
    public PacketBuffer writeInt(int value) {
        this.buffer.writeInt(value);
        return this;
    }

    @Override
    public int readInt() {
        return this.buffer.readInt();
    }

    @Override
    public PacketBuffer writeBoolean(boolean value) {
        this.buffer.writeBoolean(value);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return this.buffer.readBoolean();
    }

    @Override
    public PacketBuffer writeByte(int value) {
        this.buffer.writeByte(value);
        return this;
    }

    @Override
    public byte readByte() {
        return this.buffer.readByte();
    }

    @Override
    public int readUnsignedByte() {
        return this.buffer.readUnsignedByte();
    }

    @Override
    public PacketBuffer writeShort(int value) {
        this.buffer.writeShort(value);
        return this;
    }

    @Override
    public short readShort() {
        return this.buffer.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.buffer.readUnsignedShort();
    }

    @Override
    public PacketBuffer writeDouble(double value) {
        this.buffer.writeDouble(value);
        return this;
    }

    @Override
    public double readDouble() {
        return this.buffer.readDouble();
    }

    @Override
    public PacketBuffer writeFloat(float value) {
        this.buffer.writeFloat(value);
        return this;
    }

    @Override
    public float readFloat() {
        return this.buffer.readFloat();
    }

    @Override
    public byte[] array() {
        return this.buffer.array();
    }

    @Override
    public PacketBuffer newBuffer(ByteBuf buf) {
        return this.network.createBuffer(buf);
    }
}
