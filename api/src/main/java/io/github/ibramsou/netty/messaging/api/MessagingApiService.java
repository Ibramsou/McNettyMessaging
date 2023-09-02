package io.github.ibramsou.netty.messaging.api;

import java.util.Iterator;
import java.util.ServiceLoader;

public class MessagingApiService {

    private static final Messaging implementation;

    static {
        ServiceLoader<Messaging> service = ServiceLoader.load(Messaging.class, MessagingApiService.class.getClassLoader());
        Iterator<Messaging> iterator = service.iterator();
        if (iterator.hasNext()) {
            implementation = iterator.next();
        } else {
            throw new IllegalStateException(String.format("No %s implementation found !", Messaging.class.getSimpleName()));
        }
    }

    public static Messaging getImplementation() {
        return implementation;
    }
}
