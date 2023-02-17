package fr.bramsou.messaging.netty;

import fr.bramsou.messaging.netty.handler.PacketListenerHandler;
import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.pipeline.PipelineCompression;
import fr.bramsou.messaging.netty.registry.PacketRegistryState;
import fr.bramsou.messaging.netty.session.NettySession;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import fr.bramsou.messaging.netty.util.TaskHandler;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.Validate;

import java.net.ConnectException;
import java.util.Optional;

public class NettyNetwork extends SimpleChannelInboundHandler<NettyPacket<?>> implements TaskHandler {

    private final NettySession session;
    private Channel channel;
    private boolean disconnected;
    private PacketRegistryState state;
    private PacketListenerHandler packetListener;

    public NettyNetwork(NettySession session) {
        this.session = session;
        this.state = session.getListener().getDefaultPacketState(this);
        this.packetListener = session.getListener().getDefaultPacketListener(this);
    }

    public void sendPacket(NettyPacket<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(NettyPacket<?> packet, GenericFutureListener<? extends Future<? super Void>> listener) {
        if (this.channel == null) return;

        if (packet.hasSendingPriority()) {
            this.sendPacketInternal(packet, listener);
        } else {
            this.executeTask(() -> this.sendPacketInternal(packet, listener));
        }
    }

    private void sendPacketInternal(NettyPacket<?> packet, GenericFutureListener<? extends Future<? super Void>> listener) {
        final ChannelFuture channelFuture = this.channel.writeAndFlush(packet).addListener(future -> {
            if (future.isSuccess()) {
                packet.write(this);
            } else {
                this.exceptionCaught(null, future.cause());
            }
        });
        if (listener != null) channelFuture.addListener(listener);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        DisconnectReason reason;
        if (cause instanceof ConnectTimeoutException || (cause instanceof ConnectException && cause.getMessage().contains("connection timed out"))) {
            reason = DisconnectReason.CONNECTION_TIMED_OUT;
        } else if (cause instanceof ReadTimeoutException) {
            reason = DisconnectReason.READ_TIMED_OUT;
        } else if (cause instanceof WriteTimeoutException) {
            reason = DisconnectReason.WRITE_TIMED_OUT;
        } else {
            reason = DisconnectReason.EXCEPTION_CAUGHT;
        }

        this.disconnect(reason, cause);
    }

    public void disconnect(DisconnectReason reason) {
        this.disconnect(reason, null);
    }

    public void disconnect(DisconnectReason reason, Throwable cause) {
        if (this.disconnected) return;

        this.disconnected = true;

        if (this.channel != null && this.channel.isOpen()) {
            this.channel.flush().close();
        }

        this.session.getListener().disconnected(this, reason, cause);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        if (this.disconnected || this.channel != null) {
            ctx.channel().close();
            return;
        }

        this.channel = ctx.channel();
        this.session.getListener().connected(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        if (ctx.channel() == this.channel) {
            this.disconnect(DisconnectReason.HOST_DISCONNECTED, null);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext context, NettyPacket packet) throws Exception {
        this.getPacketListener().ifPresent(packet::read);
    }

    public void setCompressionThreshold(int threshold) {
        if (this.channel == null) return;

       final ChannelHandler handler = this.channel.pipeline().get("compression");
        if (threshold >= 0) {
            if (handler instanceof PipelineCompression) {
                ((PipelineCompression) handler).setCompressionThreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("codec", "compression", new PipelineCompression(threshold));
            }
        } else {
            if (handler instanceof PipelineCompression) {
                this.channel.pipeline().remove("compression");
            }
        }
    }

    public void close(DisconnectReason reason) {
        this.disconnect(reason, null);
    }

    public boolean isConnected() {
        return !this.disconnected && this.channel != null && this.channel.isOpen();
    }

    public Channel getChannel() {
        return channel;
    }

    public PacketRegistryState getState() {
        Validate.notNull(this.state, "Network state cannot be null");
        return state;
    }

    public Optional<PacketListenerHandler> getPacketListener() {
        return Optional.ofNullable(this.packetListener);
    }

    public void setState(PacketRegistryState state, PacketListenerHandler listener) {
        this.state = state;
        this.packetListener = listener;
    }
}
