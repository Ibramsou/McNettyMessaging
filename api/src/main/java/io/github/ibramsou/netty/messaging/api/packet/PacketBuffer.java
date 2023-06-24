package io.github.ibramsou.netty.messaging.api.packet;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.netty.buffer.ByteBuf;

public interface PacketBuffer {

    Network network();

    Session session();

    ByteBuf buffer();

    ByteBuf writeBytes(byte[] bytes);

    ByteBuf writeBytes(ByteBuf buf);

    ByteBuf readBytes(byte[] array);

    PacketBuffer writeString(String value);

    String readString();

    String readString(int capacity);

    PacketBuffer writeByteArray(byte[] value);

    byte[] readByteArray();

    PacketBuffer writeVarInt(int value);

    int readVarInt();

    PacketBuffer writeInt(int value);

    int readInt();

    PacketBuffer writeBoolean(boolean value);

    boolean readBoolean();

    PacketBuffer writeByte(int value);

    byte readByte();

    int readUnsignedByte();

    PacketBuffer writeShort(int value);

    short readShort();

    int readUnsignedShort();

    PacketBuffer writeDouble(double value);

    double readDouble();

    PacketBuffer writeFloat(float value);

    float readFloat();

    byte[] array();
}
