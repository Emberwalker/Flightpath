package io.drakon.flightpath.dispatch;

import io.drakon.flightpath.IDispatcher;
import io.drakon.flightpath.IExceptionHandler;
import io.drakon.flightpath.dispatch.bytecode.JavassistBytecodeClassBuilder;
import io.drakon.flightpath.dispatch.bytecode.IBytecodeDispatcher;
import io.drakon.flightpath.lib.Pair;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Experimental bytecode-based dispatcher.
 *
 * When using this, ensure all methods and their containing classes are <b>public</b>, as the dispatcher logic assumes
 * fields are accessible. This may be fixed in a later build.
 *
 * @author Arkan <arkan@drakon.io>
 */
public class BytecodeDispatcher extends IDispatcher {

    private Map<Class, List<Pair<Object, Set<Method>>>> listeners = new HashMap<Class, List<Pair<Object, Set<Method>>>>();
    private Map<Class, IBytecodeDispatcher> dispatchers = new HashMap<Class, IBytecodeDispatcher>();

    public BytecodeDispatcher(IExceptionHandler exceptionHandler) {
        super(exceptionHandler);
    }

    @Override
    public void addSubscribers(@Nonnull Pair<Object, Map<Class, Set<Method>>>[] subscribers) {
        Set<Class> dirty = new HashSet<Class>();
        for (Pair<Object, Map<Class, Set<Method>>> pair : subscribers) {
            Object obj = pair.a;
            for (Class cls : pair.b.keySet()) {
                List<Pair<Object, Set<Method>>> current = listeners.get(cls);
                if (current == null) current = new ArrayList<Pair<Object, Set<Method>>>();
                current.add(new Pair<Object, Set<Method>>(obj, pair.b.get(cls)));
                listeners.put(cls, current);
                dirty.add(cls);
            }
        }
        regenerateDirtyClasses(dirty);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addSubscriber(@Nonnull Object target, @Nonnull Map<Class, Set<Method>> locatedMethods) {
        addSubscribers(new Pair[]{new Pair<Object, Map<Class, Set<Method>>>(target, locatedMethods)});
    }

    @SuppressWarnings("unchecked")
    private void regenerateDirtyClasses(@Nonnull Set<Class> dirtyEvents) {
        for (Class cls : dirtyEvents) {
            Pair<Object, Set<Method>>[] targets = listeners.get(cls).toArray(new Pair[0]);
            List<Object> objs = new LinkedList<Object>();
            for (Pair<Object, Set<Method>> pair : targets) {
                objs.add(pair.a);
            }
            JavassistBytecodeClassBuilder dispatcherBuilder = new JavassistBytecodeClassBuilder(cls, (Pair<Object, Set<Method>>[])targets);
            Class<IBytecodeDispatcher> dispatchCls = dispatcherBuilder.build();
            try {
                dispatchers.put(cls, dispatchCls.getConstructor(dispatcherBuilder.getListenerTypes()).newInstance(objs.toArray()));
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dispatch(@Nonnull Object evt) {
        for (Map.Entry<Class, IBytecodeDispatcher> entry : dispatchers.entrySet()) {
            if (entry.getKey().isAssignableFrom(evt.getClass())) {
                IBytecodeDispatcher dispatcher = entry.getValue();
                try {
                    dispatcher.dispatch(evt);
                } catch (Exception ex) {
                    exceptionHandler.handle(ex);
                }
            }
        }
        exceptionHandler.flush();
    }

}
