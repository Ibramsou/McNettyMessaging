package fr.bramsou.messaging.netty.registry;

import fr.bramsou.messaging.netty.packet.NettyPacket;
import fr.bramsou.messaging.netty.packet.PacketBuffer;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {

    private static final PacketRegistry instance = new PacketRegistry();

    public static PacketRegistry getInstance() {
        return instance;
    }

    private final Map<PacketRegistryState, Map<Class<? extends NettyPacket<?>>, Integer>> statePacketIdMap = new HashMap<>();
    private final Map<PacketRegistryState, Map<Integer, PacketFactory<? extends NettyPacket<?>>>> stateIdPacketMap = new HashMap<>();
    private final PacketDirection[] directions = new PacketDirection[] {PacketDirection.SERVER_BOUND, PacketDirection.CLIENT_BOUND};

    protected void register(PacketRegistryState state, int id, Class<? extends NettyPacket<?>> packet, PacketFactory<? extends NettyPacket<?>> constructor, PacketDirection... directions) {
        if (directions == null || directions.length == 0) {
            directions = this.directions;
        }
        boolean serverBound = false;
        boolean clientBound = false;
        if (directions == this.directions) {
            serverBound = clientBound = true;
        } else {
            for (PacketDirection packetDirection : directions) {
                if (packetDirection.equals(PacketDirection.SERVER_BOUND)) serverBound = true;
                if (packetDirection.equals(PacketDirection.CLIENT_BOUND)) clientBound = true;
            }
        }

        if (serverBound) {
            this.statePacketIdMap.computeIfAbsent(state, s -> new HashMap<>()).put(packet, id);
        }
        if (clientBound) {
            this.stateIdPacketMap.computeIfAbsent(state, s -> new HashMap<>()).put(id, constructor);
        }
    }

    public PacketFactory<? extends NettyPacket<?>> getPacketFactory(PacketRegistryState state, int id, PacketBuffer buffer) {
        final Map<Integer, PacketFactory<? extends NettyPacket<?>>> map = this.stateIdPacketMap.get(state);
        if (map == null) throw new IllegalArgumentException(state.getName() + " state is not registered in packet registry !");
        final PacketFactory<? extends NettyPacket<?>> constructor = map.get(id);
        if (constructor == null) throw new IllegalArgumentException("Packet #" + id + " is not a registered packet on " + state.getName() + " state !");
        return constructor;
    }

    public int getPacketId(PacketRegistryState state, NettyPacket<?> packet) {
        final Map<Class<? extends NettyPacket<?>>, Integer> map = this.statePacketIdMap.get(state);
        if (map == null) throw new IllegalArgumentException(state.getName() + " state is not registered in packet registry !");
        final Integer id = map.get(packet.getClass());
        if (id == null) throw new IllegalArgumentException("Cannot get id from " + packet.getClass().getSimpleName() + " in " + state.getName() + " state !");
        return id;
    }
}
