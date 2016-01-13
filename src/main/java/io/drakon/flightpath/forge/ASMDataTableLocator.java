package io.drakon.flightpath.forge;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;
import io.drakon.flightpath.Airdrop;
import io.drakon.flightpath.ISubscriberLocator;
import io.drakon.flightpath.lib.AnnotationLocator;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * EXPERIMENTAL alternative locator implementation for Minecraft Forge-based mods. Classes that are intended to be event
 * handlers MUST have a ForgepathHandler annotation and zero-param constructor.
 *
 * This is untested - USE AT YOUR OWN RISK! (and report bugs please)
 *
 * @author Arkan <arkan@drakon.io>
 */
@ParametersAreNonnullByDefault
public class ASMDataTableLocator implements ISubscriberLocator {

    private static final Map<Object, Map<Class, Set<Method>>> NO_SUBSCRIBERS = new HashMap<Object, Map<Class,
            Set<Method>>>();

    private AnnotationLocator innerLocator;
    private ASMDataTable asmTable;

    /**
     * Constructor. Scans the given ASM data table for candidate event handlers carrying the given Annotation.
     *
     * @param asmTable The ASM data table (from FMLPreinitializationEvent)
     * @param ann The annotation class to look for
     */
    public ASMDataTableLocator(ASMDataTable asmTable, Class<? extends Annotation> ann) {
        this.innerLocator = new AnnotationLocator(ann);
        this.asmTable = asmTable;
    }

    /**
     * Constructor. Scans the given ASM data table for candidate event handlers carrying the Airdrop annotation.
     * @param asmTable The ASM data table (from FMLPreinitializationEvent)
     */
    public ASMDataTableLocator(ASMDataTable asmTable) {
        this(asmTable, Airdrop.class);
    }

    @Nonnull
    @Override
    public Map<Class, Set<Method>> findSubscribers(Object obj) {
        return innerLocator.findSubscribers(obj);
    }

    @Nonnull
    @Override
    public Map<Object, Map<Class, Set<Method>>> findSubscribers() {
        SetMultimap<String, ASMData> allAnnotationsInContainer = asmTable.getAnnotationsFor(
                Loader.instance().activeModContainer());
        if (!allAnnotationsInContainer.containsKey(ForgepathHandler.class.getCanonicalName())) return NO_SUBSCRIBERS;
        Set<ASMData> asmDataSet = allAnnotationsInContainer.get(ForgepathHandler.class.getName());

        // Goddamnit Java and your stupidly long types
        ImmutableMap.Builder<Object, Map<Class, Set<Method>>> mapBuilder =
                new ImmutableMap.Builder<Object, Map<Class, Set<Method>>>();

        for (ASMData asmData : asmDataSet) {
            String cname = asmData.getClassName();
            Object obj;
            try {
                obj = Class.forName(cname).newInstance();
            } catch (Exception ex) {
                continue; // SKIP!
            }
            Map<Class, Set<Method>> subscribers = innerLocator.findSubscribers(obj);
            mapBuilder.put(obj, subscribers);
        }

        return mapBuilder.build();
    }
}
