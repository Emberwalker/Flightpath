package io.drakon.flightpath.test.dispatch;

import io.drakon.flightpath.IDispatcher;
import io.drakon.flightpath.dispatch.BytecodeDispatcher;
import io.drakon.flightpath.lib.BlackholeExceptionHandler;

public class BytecodeDispatcherTests extends GenericDispatcherTests {
    @Override
    protected IDispatcher getNewDispatcher() {
        return new BytecodeDispatcher(new BlackholeExceptionHandler());
    }
}
