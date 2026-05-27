package br.com.samuel.paullo.pubsub;

/**
 *
 * @author Samuel Paulo
 */
public class DispatcherInvalidatedException extends RuntimeException {

    public DispatcherInvalidatedException() {
        super("event dispatcher was invalidated.");
    }
}
