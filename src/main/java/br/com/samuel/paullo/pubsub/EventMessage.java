package br.com.samuel.paullo.pubsub;

/**
 *
 * @author Samuel Paulo
 */
public abstract class EventMessage {

    private EventDispatcher dispatcher;
    private boolean consumed;

    void setDispatcher(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void consume() {
        if (consumed) {
            throw new EventConsumeException(this);
        }
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void forward(EventMessage chainEvent) {
        dispatcher.forward(chainEvent);
    }
    
    public void installPublisher(PublisherInstallable installable) {
        dispatcher.installPublisher(installable);
    }
}
