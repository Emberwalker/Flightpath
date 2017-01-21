package io.drakon.flightpath;

import io.drakon.flightpath.dispatch.JavaDispatcher;
import io.drakon.flightpath.lib.AnnotationLocator;
import io.drakon.flightpath.lib.BlackholeExceptionHandler;
import io.drakon.flightpath.lib.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Flightpath - an ordered event bus implementation.
 *
 * @author Arkan <arkan@drakon.io>
 */
@ParametersAreNonnullByDefault
@SuppressWarnings({"unused", "unchecked"})
public class Flightpath {

    private final ISubscriberLocator locator;
    private IDispatcher dispatcher;

    /**
     * Generates a plain old Flightpath, using the default @Airdrop annotation and Java dispatcher.
     */
    public Flightpath() {
        this(new AnnotationLocator(Airdrop.class), new JavaDispatcher(new BlackholeExceptionHandler()));
    }

    /**
     * Generates a Flightpath using a different locator implementation.
     *
     * Pass a custom constructed instance of AnnotationLocator to use alternative annotations.
     *
     * @param locator The locator to be applied to new objects.
     */
    public Flightpath(ISubscriberLocator locator) {
        this(locator, new JavaDispatcher(new BlackholeExceptionHandler()));
    }

    /**
     * Generates a Flightpath using a different dispatcher implementation.
     *
     * @param dispatcher The dispatcher which handles events.
     */
    public Flightpath(IDispatcher dispatcher) {
        this(new AnnotationLocator(Airdrop.class), dispatcher);
    }

    /**
     * Generates a Flightpath with a different dispatcher and locator implementation.
     *
     * @param locator The locator to be applied to new objects.
     * @param dispatcher The dispatcher which handles events.
     */
    public Flightpath(ISubscriberLocator locator, IDispatcher dispatcher) {
        this.locator = locator;
        this.dispatcher = dispatcher;
    }

    /**
     * Used to change exception handling behaviour. This calls #setExceptionHandler on the current dispatcher.
     *
     * @param handler The handler to use.
     */
    public void setExceptionHandler(IExceptionHandler handler) {
        dispatcher.setExceptionHandler(handler);
    }

    /**
     * Registers a given object onto the bus.
     *
     * @param obj Object to attach to the bus.
     */
    public void register(Object obj) {
        Map<Class, Set<Method>> located = locator.findSubscribers(obj);
        dispatcher.addSubscriber(obj, located);
    }

    /**
     * Requests the current locator to scan for subscribers and register all it finds. How this works is dependant
     * on the implementation of ISubscriberLocator - it may also be a no-op.
     */
    public void registerAll() {
        try {
            Set<Pair<Object, Map<Class, Set<Method>>>> objs = locator.findSubscribers();
            dispatcher.addSubscribers((Pair<Object, Map<Class, Set<Method>>>[])objs.toArray());
        } catch (AbstractMethodError err) {
            // Older implementation of ISubscriberLocator without findSubscribers(); SKIP!
        }
    }

    /**
     * Posts the given event on the bus.
     *
     * By default this blackholes exceptions. If you need different behaviour, see setExceptionHandler.
     *
     * @param evt The event to post.
     */
    public void post(Object evt) {
        dispatcher.dispatch(evt);
    }

}
