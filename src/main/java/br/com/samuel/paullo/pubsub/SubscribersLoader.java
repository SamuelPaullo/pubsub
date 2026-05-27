package br.com.samuel.paullo.pubsub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 *
 * @author Samuel Paulo
 */
public final class SubscribersLoader {

    private static final List<EventSubscriber> SUBS;

    static {
        Iterator<EventSubscriber> it = ServiceLoader
                .load(EventSubscriber.class).iterator();
        List<EventSubscriber> subscribers = new ArrayList();
        while (it.hasNext()) {
            subscribers.add(it.next());
        }
        SUBS = Collections.unmodifiableList(subscribers);
    }

    private SubscribersLoader() {
    }

    public static List<EventSubscriber> load(Class<? extends EventSubscriber> clazz) {
        return SUBS.stream()
                .filter(sub -> sub.getClass().equals(clazz))
                .collect(Collectors.toList());
    }
}
