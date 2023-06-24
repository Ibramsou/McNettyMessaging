package io.github.ibramsou.netty.messaging.api.event;

@FunctionalInterface
public interface EventSubscriber<T extends Event> {

    void onEvent(T event) throws Exception;
}
