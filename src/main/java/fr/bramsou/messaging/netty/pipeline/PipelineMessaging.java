package fr.bramsou.messaging.netty.pipeline;

import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.PacketBuffer;
import fr.bramsou.messaging.netty.registry.EnumPacketRegistry;
import fr.bramsou.messaging.netty.registry.PacketConstructor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;

import java.util.List;

public class PipelineMessaging extends ByteToMessageCodec<NettyPacket> {

    @Override
    protected void encode(ChannelHandlerContext context, NettyPacket packet, ByteBuf out) {
        int initial = out.writerIndex();

        try {
            int packetId = EnumPacketRegistry.ENCODER_PACKETS.get(packet.getClass());
            PacketBuffer buffer = new PacketBuffer(out);
            buffer.writeVarInt(packetId);
            packet.serialize(buffer);
        } catch (Throwable t) {
            out.writerIndex(initial);
            throw new RuntimeException("Unable to encode packet " + packet.getClass().getSimpleName(), t.getCause());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
        if (in.readableBytes() != 0) {
            PacketBuffer buffer = new PacketBuffer(in);
            int packetId = buffer.readVarInt();

            PacketConstructor<? extends NettyPacket> constructor = EnumPacketRegistry.DECODER_PACKET.get(packetId);

            if (constructor == null) {
                in.skipBytes(buffer.readableBytes());
                return;
            }

            NettyPacket packet;
            try {
                packet = constructor.construct(new PacketBuffer(in));
            } catch (Exception e) {
                throw new DecoderException("Cannot read packet ID: " + packetId, e);
            }

            if (in.readableBytes() > 0) {
                throw new DecoderException("Cannot read packet " + packet.getClass().getSimpleName() + " start(" + in.readableBytes() + "), end(" + in.readableBytes() + ")");
            }

            out.add(packet);
        }
    }
}
