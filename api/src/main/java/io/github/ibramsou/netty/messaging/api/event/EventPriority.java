package io.github.ibramsou.netty.messaging.api.event;

/**
 * Define an order while registered listeners are called
 * LOWEST = listener is called at end
 * HIGH = listener is called at first
 */
public enum EventPriority {
    HIGHEST,
    HIGH,
    MEDIUM,
    LOW,
    LOWEST
}
