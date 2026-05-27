package br.com.samuel.paullo.pubsub;

/**
 *
 * @author Samuel Paulo
 */
public final class OccurredError {

    private final EventSubscriber subscriber;
    private final EventMessage event;
    private final Throwable throwable;

    public OccurredError(EventSubscriber subscriber, EventMessage event, Throwable throwable) {
        this.subscriber = subscriber;
        this.event = event;
        this.throwable = throwable;
    }

    public EventSubscriber getSubscriber() {
        return subscriber;
    }

    public EventMessage getEvent() {
        return event;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
