package fr.bramsou.messaging.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PacketBuffer extends ByteBuf {

    private final ByteBuf buf;

    public static int getLengthSize(int length) {
        for(int i = 1; i < 5; ++i) {
            if ((length & -1 << i * 7) == 0) {
                return i;
            }
        }

        return 5;
    }

    public PacketBuffer(ByteBuf wrapped) {
        this.buf = wrapped;
    }

    public PacketBuffer writeString(String input) {
        byte[] array = input.getBytes(StandardCharsets.UTF_8);
        if (array.length > 32767) {
            throw new EncoderException("String too big (was " + input.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            this.writeVarInt(array.length);
            this.writeBytes(array);
        }

        return this;
    }

    public String readString() {
        return this.readString(Integer.MAX_VALUE);
    }

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

    public void writeByteArray(byte[] array) {
        this.writeVarInt(array.length);
        this.writeBytes(array);
    }

    public byte[] readByteArray() {
        byte[] array = new byte[this.readVarInt()];
        this.readBytes(array);
        return array;
    }

    public void writeVarInt(int input) {
        while ((input & -128) != 0) {
            this.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        this.writeByte(input);
    }

    public int readVarInt() {
        int value = 0;
        int size = 0;
        int b;
        while (((b = buf.readByte()) & 0x80) == 0x80) {
            value |= (b & 0x7F) << (size++ * 7);
            if (size > 5) {
                throw new DecoderException("VarInt too long (length must be <= 5)");
            }
        }

        return value | ((b & 0x7F) << (size * 7));
    }

    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    public int readInt() {
        return this.buf.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return this.buf.readUnsignedInt();
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public char readChar() {
        return this.buf.readChar();
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public ByteBuf writeByte(int input) {
        return this.buf.writeByte(input);
    }

    @Override
    public byte readByte() {
        return this.buf.readByte();
    }

    @Override
    public ByteBuf writeShort(int input) {
        return this.buf.writeShort(input);
    }

    @Override
    public ByteBuf writeMedium(int value) {
        return this.buf.writeMedium(value);
    }

    @Override
    public ByteBuf writeInt(int value) {
        return this.buf.writeInt(value);
    }

    @Override
    public ByteBuf writeLong(long value) {
        return this.buf.writeLong(value);
    }

    @Override
    public ByteBuf writeChar(int value) {
        return this.buf.writeChar(value);
    }

    @Override
    public ByteBuf writeFloat(float value) {
        return this.buf.writeFloat(value);
    }

    @Override
    public ByteBuf writeDouble(double value) {
        return this.buf.writeDouble(value);
    }

    public short readShort() {
        return this.buf.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        return this.buf.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        return this.buf.readUnsignedMedium();
    }

    @Override
    public ByteBuf writeBytes(byte[] bytes) {
        return this.buf.writeBytes(bytes);
    }

    public ByteBuf writeBytes(ByteBuf buf) {
        return this.buf.writeBytes(buf);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf, int value) {
        return this.buf.writeBytes(buf, value);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf, int start, int end) {
        return this.buf.writeBytes(buf, start, end);
    }

    @Override
    public ByteBuf writeBytes(byte[] array, int start, int end) {
        return this.buf.writeBytes(array, start, end);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return this.buf.writeBytes(src);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return this.buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return this.buf.writeBytes(in, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        return this.buf.writeZero(length);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return this.buf.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return this.buf.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return this.buf.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return this.buf.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteBufProcessor processor) {
        return this.buf.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteBufProcessor processor) {
        return this.buf.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor processor) {
        return this.buf.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
        return this.buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return this.buf.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.buf.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return this.buf.slice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.buf.slice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return this.buf.duplicate();
    }

    @Override
    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.buf.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.buf.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.buf.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.buf.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return this.buf.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return this.buf.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return this.buf.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.buf.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return this.buf.compareTo(buffer);
    }

    @Override
    public String toString() {
        return this.buf.toString();
    }

    @Override
    public ByteBuf retain(int increment) {
        return this.buf.retain(increment);
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.buf.release(decrement);
    }

    @Override
    public int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    public ByteBuf retain() {
        return this.buf.retain();
    }

    public ByteBuf readBytes(byte[] array) {
        return this.buf.readBytes(array);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return this.buf.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return this.buf.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        return this.buf.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return this.buf.readBytes(out, length);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        return this.buf.skipBytes(length);
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        return this.buf.writeBoolean(value);
    }

    public ByteBuf readBytes(int input) {
        return this.buf.readBytes(input);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.buf.readSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        return this.buf.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        return this.buf.readBytes(dst, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        return this.buf.readBytes(dst, dstIndex, length);
    }

    @Override
    public int capacity() {
        return this.buf.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        return this.buf.capacity(newCapacity);
    }

    @Override
    public int maxCapacity() {
        return this.buf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    @Override
    public ByteOrder order() {
        return this.buf.order();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        return this.buf.order(endianness);
    }

    @Override
    public ByteBuf unwrap() {
        return this.buf.unwrap();
    }

    @Override
    public boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        return this.buf.readerIndex(readerIndex);
    }

    @Override
    public int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        return this.buf.writeInt(writerIndex);
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        return this.buf.setIndex(readerIndex, writerIndex);
    }

    public int readableBytes() {
        return this.buf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.buf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.buf.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return this.buf.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return this.buf.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return this.buf.isWritable(size);
    }

    @Override
    public ByteBuf clear() {
        return this.buf.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this.buf.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this.buf.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this.buf.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this.buf.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return this.buf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return this.buf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        return this.buf.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return this.buf.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return this.buf.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return this.buf.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return this.buf.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return this.buf.getShort(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.buf.getUnsignedShort(index);
    }

    @Override
    public int getMedium(int index) {
        return this.buf.getMedium(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.buf.getUnsignedMedium(index);
    }

    @Override
    public int getInt(int index) {
        return this.buf.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.buf.getUnsignedInt(index);
    }

    @Override
    public long getLong(int index) {
        return this.buf.getLong(index);
    }

    @Override
    public char getChar(int index) {
        return this.buf.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return this.buf.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return this.buf.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        return this.buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        return this.buf.getBytes(index, dst, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        return this.buf.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        return this.buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        return this.buf.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        return this.buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        return this.buf.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.buf.getBytes(index, out, length);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        return this.buf.setBoolean(index, value);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        return this.buf.setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        return this.buf.setShort(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        return this.buf.setMedium(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        return this.buf.setInt(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        return this.buf.setLong(index, value);
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        return this.buf.setChar(index, value);
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        return this.buf.setFloat(index, value);
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        return this.buf.setDouble(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        return this.buf.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        return this.buf.setBytes(index, src, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return this.buf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return this.buf.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return this.buf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return this.buf.setBytes(index, src);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.buf.setBytes(index, in, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        return this.buf.setZero(index, length);
    }
}
