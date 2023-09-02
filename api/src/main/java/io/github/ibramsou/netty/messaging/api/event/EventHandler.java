package io.github.ibramsou.netty.messaging.api.event;

public interface EventHandler {

    <T extends Event> void subscribe(Class<T> event, EventSubscriber<T> subscriber);

    /**
     * Register an event listener
     *
     * @param event Event to register
     * @param priority define an order when every event of that type are called
     * @param subscriber the listener
     * @param <T> Objects that is inherited by {@link Event}
     */
    <T extends Event> void subscribe(Class<T> event, EventPriority priority, EventSubscriber<T> subscriber);

    /**
     * Handle all registered listeners for the specified event
     * @param event specified event
     * @param <T> Objects that is inherited by {@link Event}
     */
    <T extends Event> void post(T event);
}
