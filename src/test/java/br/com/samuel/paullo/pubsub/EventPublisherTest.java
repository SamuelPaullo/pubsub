package br.com.samuel.paullo.pubsub;

import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Samuel Paulo
 */
public class EventPublisherTest {

    @Test
    void should_publish_event_to_subscribers() {

        EventPublisher publisher = new EventPublisher();

        FirstLevelSubscriber firstLevelSubscriber = new FirstLevelSubscriber();
        SecondLevelSubscriber secondLevelSubscriber = new SecondLevelSubscriber();
        ThirdLevelSubscriber thirdLevelSubscriber = new ThirdLevelSubscriber();

        publisher.subscribe(
                firstLevelSubscriber,
                secondLevelSubscriber,
                thirdLevelSubscriber
        );

        FirstLevelEvent firstLevelEvent = new FirstLevelEvent();

        publisher.publish(firstLevelEvent);

        //this is important when the publisher is no longer useful to avoid memory leak
        publisher.unsubscribeAll();

        // verifica se o subs receberam o evento corretamente
        assertEquals(firstLevelSubscriber.eventReceived, firstLevelEvent);
        assertEquals(secondLevelSubscriber.eventReceived.getClass(), SecondLevelEvent.class);
        assertEquals(thirdLevelSubscriber.eventReceived.getClass(), ThirdLevelEvent.class);
    }

    @Test
    void should_consume_events() {

        EventPublisher publisher = new EventPublisher();

        /*
         *****************************
         * consume first level event *
         *****************************
         */
        FirstLevelSubscriber firstLevelSubscriber1 = new FirstLevelSubscriber();
        FirstLevelSubscriber firstLevelSubscriber2 = new FirstLevelSubscriber();
        FirstLevelSubscriber firstLevelSubscriber3 = new FirstLevelSubscriber();

        firstLevelSubscriber1.consumeEvent = true;
        firstLevelSubscriber1.sendNextLevelEvent = false;
        firstLevelSubscriber2.sendNextLevelEvent = false;
        firstLevelSubscriber3.sendNextLevelEvent = false;

        publisher.subscribe(
                firstLevelSubscriber1,
                firstLevelSubscriber2,
                firstLevelSubscriber3
        );

        FirstLevelEvent firstLevelEvent = new FirstLevelEvent();

        publisher.publish(firstLevelEvent);

        // processed event
        assertEquals(firstLevelSubscriber1.eventReceived, firstLevelEvent);
        // skipped because event was consumed 
        assertNull(firstLevelSubscriber2.eventReceived);
        assertNull(firstLevelSubscriber3.eventReceived);

        /*
         ******************************
         * consume second level event *
         ******************************
         */
        SecondLevelSubscriber secondLevelSubscriber1 = new SecondLevelSubscriber();
        SecondLevelSubscriber secondLevelSubscriber2 = new SecondLevelSubscriber();
        SecondLevelSubscriber secondLevelSubscriber3 = new SecondLevelSubscriber();

        firstLevelSubscriber1.sendNextLevelEvent = true;
        secondLevelSubscriber1.sendNextLevelEvent = false;
        secondLevelSubscriber2.consumeEvent = true;
        secondLevelSubscriber2.sendNextLevelEvent = false;
        secondLevelSubscriber3.sendNextLevelEvent = false;

        publisher.subscribe(
                secondLevelSubscriber1,
                secondLevelSubscriber2,
                secondLevelSubscriber3
        );

        // create new instance of first level event because the previous was consumed
        firstLevelEvent = new FirstLevelEvent();

        // emits first level event for chaining call
        publisher.publish(firstLevelEvent);

        // processed event
        assertEquals(firstLevelSubscriber1.eventReceived, firstLevelEvent);
        assertEquals(secondLevelSubscriber1.eventReceived.getClass(), SecondLevelEvent.class);
        assertEquals(secondLevelSubscriber2.eventReceived.getClass(), SecondLevelEvent.class);
        // skipped because event was consumed 
        assertNull(firstLevelSubscriber2.eventReceived);
        assertNull(firstLevelSubscriber3.eventReceived);
        assertNull(secondLevelSubscriber3.eventReceived);

        /*
         ********************************************
         * consume thid level event by an exception *
         ********************************************
         */
        ThirdLevelSubscriber thirdLevelSubscriber1 = new ThirdLevelSubscriber();
        ThirdLevelSubscriber thirdLevelSubscriber2 = new ThirdLevelSubscriber();
        ThirdLevelSubscriber thirdLevelSubscriber3 = new ThirdLevelSubscriber();

        secondLevelSubscriber2.sendNextLevelEvent = true;
        thirdLevelSubscriber1.throwException = true;

        publisher.subscribe(
                thirdLevelSubscriber1,
                thirdLevelSubscriber2,
                thirdLevelSubscriber3
        );

        // create new instance of first level event because the previous was consumed
        firstLevelEvent = new FirstLevelEvent();

        // emits first level event for chaining call and track if an exception occurred
        List<OccurredError> occurredExceptions = publisher.publish(firstLevelEvent);

        //this is important when the publisher is no longer useful to avoid memory leak
        publisher.unsubscribeAll();

        // processed event
        assertEquals(firstLevelSubscriber1.eventReceived, firstLevelEvent);
        assertEquals(secondLevelSubscriber1.eventReceived.getClass(), SecondLevelEvent.class);
        assertEquals(secondLevelSubscriber2.eventReceived.getClass(), SecondLevelEvent.class);
        assertEquals(thirdLevelSubscriber1.eventReceived.getClass(), ThirdLevelEvent.class);
        // skipped because event was consumed 
        assertNull(firstLevelSubscriber2.eventReceived);
        assertNull(firstLevelSubscriber3.eventReceived);
        assertNull(secondLevelSubscriber3.eventReceived);
        assertNull(thirdLevelSubscriber2.eventReceived);
        assertNull(thirdLevelSubscriber3.eventReceived);

        // test if the exception throwed was tracked by publisher
        assertNotNull(occurredExceptions);
        assertEquals(occurredExceptions.get(0).getThrowable(), thirdLevelSubscriber1.exception);
        assertEquals(occurredExceptions.get(0).getSubscriber(), thirdLevelSubscriber1);
        assertEquals(occurredExceptions.get(0).getEvent().getClass(), ThirdLevelEvent.class);

        // this object is only for temporary use and aways should be null
        assertNull(publisher.occurredExceptions);
    }

    @Test
    void should_install_publisher() {
        
        EventPublisher publisher = new EventPublisher();
        
        PublisherWrapper publisherWrapper = new PublisherWrapper();
        
        InstallableSubscriber installableSubscriber = new InstallableSubscriber();
        installableSubscriber.publisherWrapper = publisherWrapper;
        
        publisher.subscribe(installableSubscriber);
        
        publisher.publish(new InstallableEvent());
        
        publisher.unsubscribeAll();
        
        assertEquals(publisher, publisherWrapper.publisher);
    }

    /*
    **********************
    * Classes definition *
    **********************
     */
    //events
    public static class FirstLevelEvent extends EventMessage {
    }

    public static class SecondLevelEvent extends EventMessage {
    }

    public static class ThirdLevelEvent extends EventMessage {
    }

    //subscribers
    public static class TestSubscriber implements EventSubscriber {

        protected EventMessage eventReceived;
        protected boolean sendNextLevelEvent = true;
        protected boolean consumeEvent = false;

    }

    public static class FirstLevelSubscriber extends TestSubscriber {

        @Subscribe
        public void onEvent(FirstLevelEvent event) {
            // this condition is needed to skip consumed events
            if (event.isConsumed()) {
                return;
            }
            eventReceived = event;
            if (sendNextLevelEvent) {
                event.forward(new SecondLevelEvent());
            }
            if (consumeEvent) {
                event.consume();
            }
        }
    }

    public static class SecondLevelSubscriber extends TestSubscriber {

        @Subscribe
        public void onEvent(SecondLevelEvent event) {
            // this condition is needed to skip consumed events
            if (event.isConsumed()) {
                return;
            }
            eventReceived = event;
            if (sendNextLevelEvent) {
                event.forward(new ThirdLevelEvent());
            }
            if (consumeEvent) {
                event.consume();
            }
        }
    }

    public static class ThirdLevelSubscriber extends TestSubscriber {

        protected Exception exception = new Exception("something went wrong");
        protected boolean throwException = false;

        @Subscribe
        public void onEvent(ThirdLevelEvent event) throws Exception {
            if (event.isConsumed()) {
                return;
            }
            eventReceived = event;
            if (throwException) {
                /*its important to consume the event because even if an 
                execution is throwed the subscribers will be called*/
                event.consume();
                /*its usefull to tell the publish at end of chaining that
                somenthing failed to process*/
                throw exception;
            }
        }
    }

    // classes for installing test
    public class PublisherWrapper implements PublisherInstallable {

        protected EventPublisher publisher;

        @Override
        public void install(EventPublisher publisher) {
            this.publisher = publisher;
        }
    }
    
    public class InstallableEvent extends EventMessage {
    
    }
    
    public class InstallableSubscriber implements EventSubscriber {
        
        protected PublisherWrapper publisherWrapper;
        
        @Subscribe
        public void onEvent(InstallableEvent evt) {
            evt.installPublisher(publisherWrapper);
        }
    }
}
