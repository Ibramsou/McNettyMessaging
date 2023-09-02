package io.github.ibramsou.netty.messaging.core;

import io.github.ibramsou.netty.messaging.api.event.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public abstract class MessagingEventHandler implements EventHandler {

    private static final Comparator<? super OrderedSubscriber> comparator = Comparator.comparingInt(subscriber -> subscriber.priority.ordinal());

    private final Map<Class<? extends Event>, List<OrderedSubscriber>> subscriberMap = new HashMap<>();

    @Override
    public <T extends Event> void subscribe(Class<T> event, EventSubscriber<T> subscriber) {
        this.subscribe(event, EventPriority.MEDIUM, subscriber);
    }

    @Override
    public <T extends Event> void subscribe(Class<T> event, EventPriority priority, EventSubscriber<T> subscriber) {
        List<OrderedSubscriber> list = this.subscriberMap.computeIfAbsent(event, eventClass -> new ArrayList<>());
        list.add(new OrderedSubscriber(priority, subscriber));
        list.sort(comparator);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> void post(T event) {
        List<OrderedSubscriber> list = this.subscriberMap.get(event.getClass());
        if (list == null) {
            return;
        }

        list.forEach(subscriber -> {
            try {
                ((EventSubscriber<T>) subscriber.subscriber).onEvent(event);
            } catch (Exception e) {
                new EventException(String.format("An exception occurred while posting event %s", event.getClass()), e).printStackTrace(System.out);
            }
        });
    }

    static class OrderedSubscriber {
        private final EventPriority priority;
        private final EventSubscriber<?> subscriber;

        public OrderedSubscriber(EventPriority priority, EventSubscriber<?> subscriber) {
            this.priority = priority;
            this.subscriber = subscriber;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderedSubscriber that = (OrderedSubscriber) o;
            return Objects.equals(subscriber, that.subscriber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(subscriber);
        }

        @Override
        public String toString() {
            return "OrderedSubscriber{" +
                    "priority=" + priority +
                    ", subscriber=" + subscriber +
                    '}';
        }
    }
}
