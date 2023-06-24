package io.github.ibramsou.netty.messaging.core.network;

import io.github.ibramsou.netty.messaging.api.MessagingOptions;
import io.github.ibramsou.netty.messaging.api.network.Network;
import io.github.ibramsou.netty.messaging.api.network.NetworkState;
import io.github.ibramsou.netty.messaging.api.packet.MessagingPacket;
import io.github.ibramsou.netty.messaging.api.packet.PacketDirection;
import io.github.ibramsou.netty.messaging.api.packet.PacketFactory;
import io.github.ibramsou.netty.messaging.core.packet.MessagingPacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class MessagingNetworkState implements NetworkState {

    private final String name;
    private final Map<Class<? extends MessagingPacket>, Integer> packetIdMap = new HashMap<>();
    private final Map<Integer, PacketFactory<? extends MessagingPacket>> factoryMap = new HashMap<>();
    private final Set<Class<? extends MessagingPacket>> packetClasses = new HashSet<>();

    public MessagingNetworkState(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public NetworkState register(int id, Class<? extends MessagingPacket> packetClass, PacketFactory<? extends MessagingPacket> packetFactory, @Nullable PacketDirection... directions) {
        boolean serverBound = false;
        boolean clientBound = false;
        if (directions == null || directions.length == 0) {
            serverBound = clientBound = true;
        } else {
            for (PacketDirection packetDirection : directions) {
                if (packetDirection.equals(PacketDirection.SERVER_BOUND)) serverBound = true;
                if (packetDirection.equals(PacketDirection.CLIENT_BOUND)) clientBound = true;
            }
        }

        if (serverBound) {
            this.packetIdMap.put(packetClass, id);
        }
        if (clientBound) {
            this.factoryMap.put(id, packetFactory);
        }

        this.packetClasses.add(packetClass);
        return this;
    }

    @Override
    public Collection<Class<? extends MessagingPacket>> registeredPackets() {
        return this.packetClasses;
    }

    @Override
    public PacketFactory<? extends MessagingPacket> getPacketFactoryFromId(Network network, int id) {
        final PacketFactory<? extends MessagingPacket> factory = this.factoryMap.get(id);
        if (factory == null) {
            if (network.getSession().config().get(MessagingOptions.THROW_UNKNOWN_PACKET_ERRORS)) {
                throw new IllegalArgumentException("Packet #" + id + " is not a registered packet on " + this.name + " state !");
            }
        }

        return factory;
    }

    @Override
    public int getPacketId(MessagingPacket packet) {
        final Integer id = this.packetIdMap.get(packet.getClass());
        if (id == null) throw new IllegalArgumentException("Cannot get id from " + packet.getClass().getSimpleName() + " in " + this.name + " state !");
        return id;
    }

    @Override
    public MessagingPacket constructPacketFromId(Network network, int id, ByteBuf buf) {
        return this.constructPacketFromId(network, id, buf, true);
    }

    @Override
    public MessagingPacket constructPacketFromId(Network network, int id, ByteBuf buf, boolean skipBytes) {
        PacketFactory<? extends MessagingPacket> factory = this.getPacketFactoryFromId(network, id);
        if (factory == null) return null;
        MessagingPacket packet;
        try {
            packet = factory.construct(new MessagingPacketBuffer(buf, network));
        } catch (Exception e) {
            throw new DecoderException("Cannot read packet ID: " + id, e);
        }
        return packet;
    }
}
