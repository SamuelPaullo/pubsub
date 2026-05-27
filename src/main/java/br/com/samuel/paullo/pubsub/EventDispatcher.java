package br.com.samuel.paullo.pubsub;

import org.greenrobot.eventbus.EventBus;

/**
 *
 * @author Samuel Paulo
 */
class EventDispatcher {

    private final EventPublisher publisher;
    private final EventBus eventBus;
    private boolean invalidated;

    public EventDispatcher(EventPublisher publisher, EventBus eventBus) {
        this.publisher = publisher;
        this.eventBus = eventBus;
    }

    public void forward(EventMessage event) throws DispatcherInvalidatedException {
        if (!invalidated) {
            event.setDispatcher(this);
            eventBus.post(event);
        } else {
            throw new DispatcherInvalidatedException();
        }
    }

    public void invalidate() {
        invalidated = true;
    }
    
    void installPublisher(PublisherInstallable installable) {
        installable.install(publisher);
    }
}
