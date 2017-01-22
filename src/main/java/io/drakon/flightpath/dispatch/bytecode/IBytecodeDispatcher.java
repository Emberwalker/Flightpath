package io.drakon.flightpath.dispatch.bytecode;

/**
 * Do not create directly - objects spawned by the Bytecode Dispatcher use this interface.
 */
public interface IBytecodeDispatcher {

    void dispatch(Object event);

}
