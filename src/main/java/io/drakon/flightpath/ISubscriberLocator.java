package io.drakon.flightpath;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Interface for defining custom subscriber scanners.
 *
 * @author Arkan <arkan@drakon.io>
 */
@ParametersAreNonnullByDefault
public interface ISubscriberLocator {

    /**
     * Find all subscription methods on the given object.
     *
     * @param obj Object to scan for subscribers
     * @return A map of event classes -> methods listening for them
     */
    @Nonnull
    Map<Class, Set<Method>> findSubscribers(Object obj);

    /**
     * Scan for any subscribers anywhere in the current JVM. This will often simply return an empty map. Implementations
     * should construct any required objects and return them to Flightpath along with their subscription data.
     *
     * @return A map of handlers to a map of event classes -> methods listening for them
     */
    @Nonnull
    Map<Object, Map<Class, Set<Method>>> findSubscribers();

}
