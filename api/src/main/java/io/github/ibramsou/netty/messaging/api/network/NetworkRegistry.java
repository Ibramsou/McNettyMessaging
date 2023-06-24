package io.github.ibramsou.netty.messaging.api.network;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
public interface NetworkRegistry {

    /**
     * Register a new network state
     * @param name name of the state
     * @return an instance of {@link NetworkState}
     */
    NetworkState register(String name);

    /**
     * Get a registered network state
     * @param name name of the state
     * @return the registered instance of {@link NetworkState}
     */
    NetworkState getState(String name);

    /**
     * Get a collection of all registered network states
     * @return a collection
     */
    Collection<? extends NetworkState> getStates();
}
