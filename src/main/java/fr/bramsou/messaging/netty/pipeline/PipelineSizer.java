package fr.bramsou.messaging.netty.pipeline;

import fr.bramsou.messaging.netty.packet.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class PipelineSizer extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        int length = in.readableBytes();
        int lengthSize = PacketBuffer.getLengthSize(length);
        if (lengthSize > 3) {
            throw new IllegalArgumentException("unable to fit " + length + " into " + 3);
        } else {
            PacketBuffer buffer = new PacketBuffer(out);
            buffer.ensureWritable(lengthSize + length);
            buffer.writeVarInt(length);
            out.writeBytes(in, in.readerIndex(), length);
        }

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        buf.markReaderIndex();
        byte[] lengthBytes = new byte[3];
        for (int index = 0; index < lengthBytes.length; index++) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return;
            }

            lengthBytes[index] = buf.readByte();
            if (lengthBytes[index] >= 0) {
                PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(lengthBytes));

                try {
                    int length = buffer.readVarInt();
                    if (buf.readableBytes() < length) {
                        buf.resetReaderIndex();
                        return;
                    }

                    out.add(buf.readBytes(length));
                } finally {
                    buffer.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("Length is too long.");
    }
}
