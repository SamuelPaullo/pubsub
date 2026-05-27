package br.com.samuel.paullo.pubsub;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.isNull;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;

/**
 *
 * @author Samuel Paulo
 */
public class EventPublisher {

    private static EventPublisher DEFAULT_INSTANCE;

    private final EventBus eventBus;
    private final List<EventSubscriber> subscribers = new ArrayList<>();
    protected List<OccurredError> occurredExceptions;

    public EventPublisher() {
        eventBus = EventBus.builder()
                .logSubscriberExceptions(false)
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .build();
        eventBus.register(EventPublisher.this);
    }

    public void subscribe(EventSubscriber... subs) {
        for (EventSubscriber subscriber : subs) {
            eventBus.register(subscriber);
            subscribers.add(subscriber);
        }
    }

    public void unsubscribe(EventSubscriber... subs) {
        for (EventSubscriber subscriber : subs) {
            eventBus.unregister(subscriber);
            subscribers.remove(subscriber);
        }
    }

    public void unsubscribeAll() {
        unsubscribe(subscribers.toArray(new EventSubscriber[]{}));
    }

    public List<OccurredError> publish(EventMessage event) {

        occurredExceptions = new ArrayList<>();

        EventDispatcher dispatcher = new EventDispatcher(this, eventBus);
        dispatcher.forward(event);
        dispatcher.invalidate();

        List<OccurredError> response = occurredExceptions;
        occurredExceptions = null;

        return response;
    }

    @Subscribe
    public void onException(SubscriberExceptionEvent exEvent) {
        occurredExceptions.add(
                new OccurredError(
                        (EventSubscriber) exEvent.causingSubscriber,
                        (EventMessage) exEvent.causingEvent,
                        exEvent.throwable
                )
        );
    }

    public static EventPublisher getDefaultInstance() {
        if (isNull(DEFAULT_INSTANCE)) {
            DEFAULT_INSTANCE = new EventPublisher();
        }
        return DEFAULT_INSTANCE;
    }
}
