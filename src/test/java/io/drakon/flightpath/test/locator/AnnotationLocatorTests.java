package io.drakon.flightpath.test.locator;

import io.drakon.flightpath.Airdrop;
import io.drakon.flightpath.lib.AnnotationLocator;
import io.drakon.flightpath.test.DummyEvent;
import org.junit.Test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class AnnotationLocatorTests {

    @Test
    public void testLocatorSingleClass() throws NoSuchMethodException {
        AnnotationTestClass testClass = new AnnotationTestClass();
        Map<Class, Set<Method>> ret = new AnnotationLocator(Airdrop.class).findSubscribers(testClass);

        assertTrue(ret.containsKey(Object.class) && ret.containsKey(DummyEvent.class));

        Method one = AnnotationTestClass.class.getDeclaredMethod("one", Object.class);
        Method two = AnnotationTestClass.class.getDeclaredMethod("two", DummyEvent.class);
        assertTrue(ret.get(Object.class).size() == 1 && ret.get(Object.class).contains(one));
        assertTrue(ret.get(DummyEvent.class).size() == 1 && ret.get(DummyEvent.class).contains(two));
    }

    private class AnnotationTestClass {
        @Airdrop
        public void one(Object event) {}

        @Airdrop
        public void two(DummyEvent event) {}
    }

}
