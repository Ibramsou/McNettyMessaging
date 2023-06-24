package io.github.ibramsou.netty.messaging.core.packet;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.StandardCharsets;

public class MessagingPacketBuffer implements PacketBuffer {

    private final ByteBuf buffer;
    private final Network network;

    public MessagingPacketBuffer(ByteBuf buffer, Network network) {
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
        if (array.length > 32767) {
            throw new EncoderException("String too big (was " + value.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            this.writeVarInt(array.length);
            this.writeBytes(array);
        }

        return this;
    }

    @Override
    public String readString() {
        return null;
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
    public PacketBuffer writeVarInt(int value) {
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            writeShort((value & 0x7F | 0x80) << 8 | (value >>> 7));
        } else {
            writeVarInt_(value);
        }
        return this;
    }

    private void writeVarInt_(int input) {
        while ((input & -128) != 0) {
            this.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        this.writeByte(input);
    }

    @Override
    public int readVarInt() {
        int value = 0;
        int size = 0;
        int b;
        while (((b = this.buffer.readByte()) & 0x80) == 0x80) {
            value |= (b & 0x7F) << (size++ * 7);
            if (size > 5) {
                throw new DecoderException("VarInt too long (length must be <= 5)");
            }
        }

        return value | ((b & 0x7F) << (size * 7));
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
}
