package br.com.samuel.paullo.pubsub;

/**
 *
 * @author Samuel Paulo
 */
public class EventConsumeException extends RuntimeException {

    public EventConsumeException(EventMessage event) {
        super("event " + event.getClass().getName() + " already consumed");
    }
}
