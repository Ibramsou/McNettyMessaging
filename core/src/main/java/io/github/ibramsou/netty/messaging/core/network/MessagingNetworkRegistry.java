package io.github.ibramsou.netty.messaging.core.network;

import io.github.ibramsou.netty.messaging.api.network.NetworkRegistry;
import io.github.ibramsou.netty.messaging.api.network.NetworkState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class MessagingNetworkRegistry implements NetworkRegistry {

    private final Map<String, NetworkState> stateMap = new HashMap<>();

    @Override
    public NetworkState register(String name) {
        NetworkState state = new MessagingNetworkState(name);
        this.stateMap.put(state.getName(), state);
        return state;
    }

    @Override
    public NetworkState getState(String name) {
        return this.stateMap.get(name);
    }

    @Override
    public Collection<? extends NetworkState> getStates() {
        return this.stateMap.values();
    }
}
