package fr.bramsou.messaging.netty;

import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.session.NettySession;
import fr.bramsou.messaging.netty.util.DisconnectReason;
import fr.bramsou.messaging.netty.util.TaskHandler;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;

import java.net.ConnectException;
public class NettyNetwork extends SimpleChannelInboundHandler<NettyPacket> implements TaskHandler {

    private final NettySession session;
    private Channel channel;
    private boolean disconnected;

    public NettyNetwork(NettySession session) {
        this.session = session;
    }

    public void sendPacket(NettyPacket packet) {
        if (this.channel == null) return;

        if (packet.hasSendingPriority()) {
            this.sendPacketInternal(packet);
        } else {
            this.executeTask(() -> this.sendPacketInternal(packet));
        }
    }

    private void sendPacketInternal(NettyPacket packet) {
        this.channel.writeAndFlush(packet).addListener(future -> {
            if (future.isSuccess()) {
                packet.write(this);
            } else {
                this.exceptionCaught(null, future.cause());
            }
        });
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

        this.session.disconnected(this, reason, cause);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        if (this.disconnected || this.channel != null) {
            ctx.channel().close();
            return;
        }

        this.channel = ctx.channel();
        this.session.connected(this);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        if (ctx.channel() == this.channel) {
            this.disconnect(DisconnectReason.HOST_DISCONNECTED, null);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, NettyPacket packet) throws Exception {
        packet.read(this);
    }

    public boolean isConnected() {
        return !this.disconnected && this.channel != null && this.channel.isOpen();
    }

    public Channel getChannel() {
        return channel;
    }
}
