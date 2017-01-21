package io.drakon.flightpath;

import io.drakon.flightpath.lib.Pair;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Interface defining an event dispatch provider.
 *
 * Dispatchers are responsable for accepting and dispatching events to all subscribers, in an ordered manner.
 */
public abstract class IDispatcher {

    protected IExceptionHandler exceptionHandler;

    /**
     * Any manipulation to internal state should acquire this lock, via synchronised() or similar.
     */
    protected final Object lock = new Object();

    public IDispatcher(@Nonnull IExceptionHandler exceptionHandler) {
        setExceptionHandler(exceptionHandler);
    }

    public void setExceptionHandler(@Nonnull IExceptionHandler exceptionHandler) {
        synchronized (lock) {
            this.exceptionHandler = exceptionHandler;
        }
    }

    /**
     * Register mutliple listeners at once. Pair components are the same as a standard call to addSubscriber.
     *
     * Callers can use this to allow the dispatcher to optimise rebuilding its internal lists as a batch job. By
     * default, this just registers every entry by iteration - check the dispatcher documentation for if this is used.
     *
     * @param subscribers Set of pairs, containing the arguments to addSubscriber in each entry.
     */
    public void addSubscribers(@Nonnull Pair<Object, Map<Class, Set<Method>>>... subscribers) {
        for (Pair<Object, Map<Class, Set<Method>>> entry : subscribers) {
            addSubscriber(entry.a, entry.b);
        }
    }

    /**
     * Adds the discovered map from an instance of @link{ISubscriberLocator} and the object instance into this
     * dispatcher.
     *
     * @param target The object instance to register.
     * @param locatedMethods The map from ISubscriberLocator#findSubscribers.
     */
    public abstract void addSubscriber(@Nonnull Object target, @Nonnull Map<Class, Set<Method>> locatedMethods);

    /**
     * Dispatch an event to all registered listeners, ordered on subscription time.
     *
     * @param evt The event to dispatch.
     */
    public abstract void dispatch(@Nonnull Object evt);

}
