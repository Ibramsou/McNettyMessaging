package fr.bramsou.netty.messaging.registry;

import fr.bramsou.netty.messaging.packet.MessagingPacket;
import fr.bramsou.netty.messaging.packet.PacketBuffer;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {

    private static final PacketRegistry instance = new PacketRegistry();

    public static PacketRegistry getInstance() {
        return instance;
    }

    private final Map<PacketRegistryState, Map<Class<? extends MessagingPacket<?>>, Integer>> statePacketIdMap = new HashMap<>();
    private final Map<PacketRegistryState, Map<Integer, PacketFactory<? extends MessagingPacket<?>>>> stateIdPacketMap = new HashMap<>();
    private final PacketDirection[] directions = new PacketDirection[] {PacketDirection.SERVER_BOUND, PacketDirection.CLIENT_BOUND};

    protected void register(PacketRegistryState state, int id, Class<? extends MessagingPacket<?>> packet, PacketFactory<? extends MessagingPacket<?>> constructor, PacketDirection... directions) {
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

    public PacketFactory<? extends MessagingPacket<?>> getPacketFactory(PacketRegistryState state, int id, PacketBuffer buffer) {
        final Map<Integer, PacketFactory<? extends MessagingPacket<?>>> map = this.stateIdPacketMap.get(state);
        if (map == null) throw new IllegalArgumentException(state.getName() + " state is not registered in packet registry !");
        final PacketFactory<? extends MessagingPacket<?>> constructor = map.get(id);
        if (constructor == null) throw new IllegalArgumentException("Packet #" + id + " is not a registered packet on " + state.getName() + " state !");
        return constructor;
    }

    public int getPacketId(PacketRegistryState state, MessagingPacket<?> packet) {
        final Map<Class<? extends MessagingPacket<?>>, Integer> map = this.statePacketIdMap.get(state);
        if (map == null) throw new IllegalArgumentException(state.getName() + " state is not registered in packet registry !");
        final Integer id = map.get(packet.getClass());
        if (id == null) throw new IllegalArgumentException("Cannot get id from " + packet.getClass().getSimpleName() + " in " + state.getName() + " state !");
        return id;
    }
}
