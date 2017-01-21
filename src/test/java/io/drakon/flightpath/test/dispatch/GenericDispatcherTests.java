package io.drakon.flightpath.test.dispatch;

import io.drakon.flightpath.Airdrop;
import io.drakon.flightpath.IDispatcher;
import io.drakon.flightpath.test.DummyEvent;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public abstract class GenericDispatcherTests {

    protected abstract IDispatcher getNewDispatcher();

    @Test
    public void testDispatcherOnObject() throws NoSuchMethodException {
        IDispatcher dispatch = getNewDispatcher();
        DispatcherTestListener listener = new DispatcherTestListener();

        Map<Class, Set<Method>> mapping = new HashMap<Class, Set<Method>>();
        Set<Method> methods = new HashSet<Method>();
        methods.add(DispatcherTestListener.class.getMethod("test", DummyEvent.class));
        mapping.put(DummyEvent.class, methods);

        dispatch.addSubscriber(listener, mapping);
        dispatch.dispatch(new DummyEvent());

        assertTrue(listener.pass);
    }

    private class DispatcherTestListener {
        boolean pass = false;

        @Airdrop
        public void test(DummyEvent evt) {
            pass = true;
        }
    }

}
