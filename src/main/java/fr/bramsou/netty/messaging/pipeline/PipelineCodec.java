package fr.bramsou.netty.messaging.pipeline;

import fr.bramsou.netty.messaging.MessagingNetwork;
import fr.bramsou.netty.messaging.MessagingOptions;
import fr.bramsou.netty.messaging.packet.MessagingPacket;
import fr.bramsou.netty.messaging.packet.PacketBuffer;
import fr.bramsou.netty.messaging.registry.PacketFactory;
import fr.bramsou.netty.messaging.registry.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;

import java.util.List;

public class PipelineCodec extends ByteToMessageCodec<MessagingPacket<?>> {

    private final MessagingNetwork network;

    public PipelineCodec(MessagingNetwork network) {
        this.network = network;
    }

    @Override
    protected void encode(ChannelHandlerContext context, MessagingPacket<?> packet, ByteBuf out) {
        int initial = out.writerIndex();

        try {
            int packetId = PacketRegistry.getInstance().getPacketId(this.network.getState(), packet);
            PacketBuffer buffer = new PacketBuffer(out);
            buffer.writeVarInt(packetId);
            packet.serialize(buffer);
        } catch (Throwable t) {
            out.writerIndex(initial);
            throw new RuntimeException("Unable to encode packet " + packet.getClass().getSimpleName(), t);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
        if (in.readableBytes() != 0) {
            PacketBuffer buffer = new PacketBuffer(in);
            final int packetId = buffer.readVarInt();
            final PacketFactory<? extends MessagingPacket<?>> constructor = PacketRegistry.getInstance().getPacketFactory(this.network.getState(), packetId, MessagingOptions.THROW_UNKNOWN_PACKET_ERRORS);

            if (constructor == null) {
                in.skipBytes(buffer.readableBytes());
                return;
            }

            MessagingPacket<?> packet;
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
