package io.github.ibramsou.netty.messaging.api.network;

import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketBuffer;
import io.github.ibramsou.netty.messaging.api.session.Session;
import io.github.ibramsou.netty.messaging.api.util.DisconnectReason;
import io.github.ibramsou.netty.messaging.api.util.TaskHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Network extends TaskHandler {

    /**
     * @return the instance of {@link Network}
     */

    Session getSession();

    /**
     * Change the current network state
     * @param state specify the new {@link NetworkState}
     */
    void setState(NetworkState state);

    NetworkState getState();

    /**
     * Set the compression threshold
     * @param threshold an integer
     */
    void setCompressionThreshold(int threshold);

    /**
     * Send a packet to a client or a server
     * @param packet an instance of {@link MessagingPacket}
     */
    void sendPacket(MessagingPacket packet);

    /**
     * Send a packet to a client or a server
     * @param packet an instance of {@link MessagingPacket}
     * @param listener listener called after sending of the packet
     */
    void sendPacket(MessagingPacket packet, GenericFutureListener<? extends Future<? super Void>> listener);

    /**
     * get channel handler of the messaging networkt;
     * @return an instance of {@link SimpleChannelInboundHandler} of {@link MessagingPacket}
     */
    SimpleChannelInboundHandler<MessagingPacket> handle();

    /**
     * Disconnect the network session
     * @param reason specify a reason for the disconnection
     */
    void disconnect(@Nullable DisconnectReason reason);

    /**
     * Disconnect the network session
     * @param reason specify a reason for the disconnection
     * @param cause specify an error for the disconnection
     */
    void disconnect(@Nullable DisconnectReason reason, @Nullable Throwable cause);

    /**
     * Disconnect the network session
     * @param message specify a message for the disconnection
     */
    void disconnect(@Nullable String message);

    /**
     * Disconnect the network session
     * @param message specify a message for the disconnection
     * @param reason specify a reason for the disconnection
     * @param cause specify an error for the disconnection
     */
    void disconnect(@Nullable String message, @Nullable DisconnectReason reason, @Nullable Throwable cause);

    /**
     * Close the connection without calling disconnect event
     * @param reason specify a reason for the disconnection
     */
    void close(@Nullable DisconnectReason reason);

    /**
     * Get if connection is still open
     * @return true of false
     */
    boolean isConnected();

    /**
     * Get channel connected to the network
     * @return an instance of {@link Channel}
     */
    @Nullable
    Channel getChannel();

    /**
     * Create a new packet buffer
     *
     * @param buffer byte buffer
     * @return an instance of {@link PacketBuffer}
     */
    PacketBuffer createBuffer(ByteBuf buffer);

    /**
     * Get var int size
     *
     * @param length size length
     * @return integer
     */
    int getVarIntSize(int length);
}
