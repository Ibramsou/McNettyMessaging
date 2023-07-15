package io.github.ibramsou.netty.messaging.core.pipeline;

import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;
import io.github.ibramsou.netty.messaging.api.pipeline.PipelineHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class PipelineSizer extends ByteToMessageCodec<ByteBuf> implements PipelineHandler {

    private final Network network;

    public PipelineSizer(Network network) {
        this.network = network;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        int length = in.readableBytes();
        int lengthSize = this.network.getVarIntSize(length);
        if (lengthSize > 3) {
            throw new IllegalArgumentException("unable to fit " + length + " into " + 3);
        } else {
            PacketBuffer buffer = this.network.createBuffer(out);
            buffer.buffer().ensureWritable(lengthSize + length);
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
                PacketBuffer buffer = this.network.createBuffer(Unpooled.wrappedBuffer(lengthBytes));

                try {
                    int length = buffer.readVarInt();
                    if (buf.readableBytes() < length) {
                        buf.resetReaderIndex();
                        return;
                    }

                    out.add(buf.readBytes(length));
                } finally {
                    buffer.buffer().release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("Length is too long.");
    }
}
