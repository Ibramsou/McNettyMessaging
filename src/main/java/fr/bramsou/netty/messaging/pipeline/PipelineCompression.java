package fr.bramsou.netty.messaging.pipeline;

import fr.bramsou.netty.messaging.packet.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PipelineCompression extends ByteToMessageCodec<ByteBuf> {

    private static final int MAX_COMPRESSED_SIZE = 2097152;

    private int threshold;
    private final Inflater inflater;
    private final Deflater deflater;
    private final byte[] buffer;

    public PipelineCompression(int threshold) {
        this.threshold = threshold;
        this.inflater = new Inflater();
        this.deflater = new Deflater();
        this.buffer = new byte[8192];
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        int size = in.readableBytes();
        PacketBuffer buffer = new PacketBuffer(out);

        if (size < this.threshold) {
            buffer.writeVarInt(0);
            buffer.writeBytes(in);
        } else {
            byte[] array = new byte[size];
                in.readBytes(array);
            buffer.writeVarInt(array.length);
            this.deflater.setInput(array, 0, size);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                int i = this.deflater.deflate(this.buffer);
                buffer.writeBytes(this.buffer, 0, i);
            }

            this.deflater.reset();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            PacketBuffer buffer = new PacketBuffer(in);
            int size = buffer.readVarInt();

            if (size == 0) {
                out.add(buffer.readBytes(buffer.readableBytes()));
            } else {
                if (size < this.threshold) {
                    throw new DecoderException("Badly compressed packet - size of " + size + " is below server threshold of " + this.threshold);
                }

                if (size > MAX_COMPRESSED_SIZE) {
                    throw new DecoderException("Badly compressed packet - size of " + size + " is larger than protocol maximum of " + MAX_COMPRESSED_SIZE);
                }

                byte[] array = new byte[buffer.readableBytes()];
                buffer.readBytes(array);
                this.inflater.setInput(array);
                byte[] inflaterArray = new byte[size];
                this.inflater.inflate(inflaterArray);
                out.add(Unpooled.wrappedBuffer(inflaterArray));
                this.inflater.reset();
            }
        }
    }

    public void setCompressionThreshold(int threshold) {
        this.threshold = threshold;
    }
}
