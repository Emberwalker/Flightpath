package io.drakon.flightpath.dispatch.bytecode;

import io.drakon.flightpath.lib.Pair;
import javassist.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Javassist provider for BytecodeDispatcher.
 *
 * @author Arkan <arkan@drakon.io>
 */
@ParametersAreNonnullByDefault
public class JavassistBytecodeClassBuilder {

    private static final String DISPATCH_BODY_START = "{\n$EVENT_TYPE evt = ($EVENT_TYPE) $1;\n";
    private static final String DISPATCH_BODY_END = "}";
    private static final String DISPATCH_METHOD_ENTRY = "$VAR.$METHOD(evt);\n";

    private final Pair<Object, Set<Method>>[] listeners;
    private final Class evtClass;

    public JavassistBytecodeClassBuilder(Class evtClass, Pair<Object, Set<Method>>... listeners) {
        this.evtClass = evtClass;
        this.listeners = listeners;
    }

    /**
     * Build the dispatcher this builder represents.
     *
     * @return The generated IBytecodeDispatcher class.
     */
    @SuppressWarnings("unchecked")
    public Class<IBytecodeDispatcher> build() {
        String _basename = "io.drakon.flightpath.dispatch.bytecode$GeneratedEvtDispatch_" + evtClass.getCanonicalName();
        ClassPool classPool = ClassPool.getDefault();
        String basename = _basename;
        int nameAttempt = 0;
        while (classPool.getOrNull(basename) != null) {
            basename = _basename + "_" + nameAttempt;
            nameAttempt += 1;
        }
        try {
            // Get CtClass objects and prep
            CtClass ctClass = classPool.makeClass(basename);
            CtClass iface = classPool.get("io.drakon.flightpath.dispatch.bytecode.IBytecodeDispatcher");
            ctClass.setInterfaces(new CtClass[]{iface});
            CtClass objectCtClass = classPool.get(Object.class.getName());

            // Build list of types required for ctor.
            Class[] listenerTypesJava = getListenerTypes();
            List<CtClass> listenerTypes = new LinkedList<CtClass>();
            for (Class cls : listenerTypesJava) {
                listenerTypes.add(classPool.get(cls.getName()));
            }

            // Create ctor.
            CtConstructor constr = CtNewConstructor.skeleton(listenerTypes.toArray(new CtClass[0]), null, ctClass);
            ctClass.addConstructor(constr);

            // Create fields.
            for (int i = 0; i < listenerTypes.size(); i++) {
                CtField.Initializer init = CtField.Initializer.byParameter(i);
                CtField field = new CtField(listenerTypes.get(i), "_" + i, ctClass);
                ctClass.addField(field, init);
            }

            // Generate dispatch method
            CtMethod dispatchMethod = new CtMethod(CtClass.voidType, "dispatch", new CtClass[]{objectCtClass}, ctClass);
            String dispatchBody = DISPATCH_BODY_START.replace("$EVENT_TYPE", evtClass.getName());
            int index = 0;
            for (Pair<Object, Set<Method>> pair : listeners) {
                for (Method m : pair.b) {
                    dispatchBody += DISPATCH_METHOD_ENTRY.
                            replace("$VAR", "_" + index).
                            replace("$METHOD", m.getName());
                }
            }
            dispatchBody += DISPATCH_BODY_END;
            dispatchMethod.setBody(dispatchBody);
            ctClass.addMethod(dispatchMethod);

            // Dump finished class and release from the ClassPool (as we don't edit existing generated classes)
            Class out = ctClass.toClass();
            ctClass.detach();

            return out;
        } catch (Exception ex) {
            // TODO: More nuanced handling of errors.
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets the array of Classes which represent the types contained in this builder, in ctor order.
     *
     * @return Ordered Class[] of types in ctor of a finished class from this builder.
     */
    public Class[] getListenerTypes() {
        List<Class> listenerTypes = new LinkedList<Class>();
        for (Pair<Object, Set<Method>> pair : listeners) {
            listenerTypes.add(pair.a.getClass());
        }
        return listenerTypes.toArray(new Class[0]);
    }

}
