package io.github.ibramsou.netty.messaging.core.network;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.event.session.SessionConnectEvent;
import io.github.ibramsou.netty.messaging.api.event.session.SessionDisconnectEvent;
import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.network.NetworkState;
import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.util.DisconnectReason;
import io.github.ibramsou.netty.messaging.core.pipeline.PipelineCompression;
import io.github.ibramsou.netty.messaging.core.session.AbstractSession;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.ConnectException;

@ParametersAreNonnullByDefault
public class MessagingNetwork extends SimpleChannelInboundHandler<MessagingPacket> implements Network  {

    private final Messaging messaging;
    private final AbstractSession session;
    private Channel channel;
    private boolean disconnected;
    private NetworkState state;

    public MessagingNetwork(Messaging messaging, AbstractSession session) {
        this.messaging = messaging;
        this.session = session;
        this.state = session.config().get(MessagingOptions.DEFAULT_NETWORK_STATE);
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public void setState(NetworkState state) {
        this.state = state;
    }

    @Override
    public NetworkState getState() {
        return this.state;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        if (this.disconnected || this.channel != null) {
            ctx.channel().close();
            return;
        }

        this.channel = ctx.channel();
        this.messaging.post(new SessionConnectEvent(this.session, this));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        if (ctx.channel() == this.channel) {
            this.disconnect(DisconnectReason.HOST_DISCONNECTED, null);
        }
    }

    @Override
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

    @Override
    public void sendPacket(MessagingPacket packet) {
        this.sendPacket(packet, null);
    }

    @Override
    public void sendPacket(MessagingPacket packet, @Nullable GenericFutureListener<? extends Future<? super Void>> listener) {
        if (this.channel == null) return;

        if (packet.hasSendingPriority()) {
            this.sendPacketInternal(packet, listener);
        } else {
            this.executeTask(() -> this.sendPacketInternal(packet, listener));
        }
    }

    private void sendPacketInternal(MessagingPacket packet, @Nullable GenericFutureListener<? extends Future<? super Void>> listener) {
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
    public SimpleChannelInboundHandler<MessagingPacket> handle() {
        return this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, MessagingPacket packet) {
        this.messaging.post(packet);
    }

    @Override
    public void exceptionCaught(@Nullable ChannelHandlerContext ctx, @Nullable Throwable cause) {
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


        this.disconnect(reason.getMessage(), reason, cause);
    }

    @Override
    public void disconnect(@Nullable DisconnectReason reason) {
        this.disconnect(reason == null ? null : reason.getMessage(), reason, null);
    }

    @Override
    public void disconnect(@Nullable DisconnectReason reason, @Nullable Throwable cause) {
        this.disconnect(reason == null ? null : reason.getMessage(), reason, cause);
    }

    @Override
    public void disconnect(@Nullable String message) {
        this.disconnect(message, null, null);
    }

    @Override
    public void disconnect(@Nullable String message, @Nullable DisconnectReason reason, @Nullable Throwable cause) {
        if (this.disconnected) return;

        this.disconnected = true;

        if (this.channel != null && this.channel.isOpen()) {
            this.channel.flush().close();
        }

        this.messaging.post(new SessionDisconnectEvent(this.session, this, reason, message, cause));
    }

    @Override
    public void close(@Nullable DisconnectReason reason) {
        this.disconnect(reason, null);
    }

    @Override
    public boolean isConnected() {
        return !this.disconnected && this.channel != null && this.channel.isOpen();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }
}
