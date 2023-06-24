package io.github.ibramsou.netty.messaging.api.network;

import io.github.ibramsou.netty.messaging.api.Messaging;
import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketDirection;
import io.github.ibramsou.netty.messaging.api.packet.PacketFactory;
import io.github.ibramsou.netty.messaging.api.packet.impl.CompressionPacket;
import io.github.ibramsou.netty.messaging.api.packet.impl.JsonMessagePacket;
import io.github.ibramsou.netty.messaging.api.packet.impl.MessagePacket;
import io.github.ibramsou.netty.messaging.api.packet.impl.TokenPacket;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
public interface NetworkState {

    /**
     * Default network state configuration
     */
    NetworkState DEFAULT_STATE = Messaging.getInstance().getRegistry().register("DEFAULT")
            .register(0x01, TokenPacket.class, TokenPacket::new)
            .register(0x02, CompressionPacket.class, CompressionPacket::new)
            .register(0x03, MessagePacket.class, MessagePacket::new)
            .register(0x04, JsonMessagePacket.class, JsonMessagePacket::new);

    /**
     * Get the name of the network state
     * @return a string
     */
    String getName();

    /**
     * Register a new custom packet to the network state
     * @param id unique id of the packet (only that custom packet can be registered by this id)
     * @param packetClass class of the custom packet
     * @param packetFactory packet factory that permit to mc netty messaging to create a new instance of the incoming packet
     * @param directions directions of the packet (incoming / outgoing), if null it will both
     * @return the network state instance
     */
    NetworkState register(
            int id,
            Class<? extends MessagingPacket> packetClass,
            PacketFactory<? extends MessagingPacket> packetFactory,
            @Nullable PacketDirection... directions);

    /**
     * Get a collection of every registered packet
     * @return a collection
     */
    Collection<Class<? extends MessagingPacket>> registeredPackets();

    /**
     * Get the registered packet factory for the specified id
     * @param network incoming connected network
     * @param id id of the packet
     * @return an implementation of {@link PacketFactory}
     */
    @Nullable
    PacketFactory<? extends MessagingPacket> getPacketFactoryFromId(Network network, int id);

    /**
     * Create an instance of messaging packet from its id
     * @param network incomming connected network
     * @param id id of the packet
     * @param buf packet buffer
     * @return an instance of {@link MessagingPacket}
     */
    @Nullable
    MessagingPacket constructPacketFromId(Network network, int id, ByteBuf buf);

    /**
     * Create an instance of messaging packet from its id
     * @param network incomming connected network
     * @param id id of the packet
     * @param buf packet buffer
     * @param skipBytes will skip sent bytes if true
     * @return an instance of {@link MessagingPacket}
     */
    @Nullable
    MessagingPacket constructPacketFromId(Network network, int id, ByteBuf buf, boolean skipBytes);

    /**
     * Get if od an outgoing packet
     * @param packet custom packet implementation
     * @return an unique integer id
     */
    int getPacketId(MessagingPacket packet);
}
