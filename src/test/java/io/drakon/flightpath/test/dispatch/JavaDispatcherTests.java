package io.drakon.flightpath.test.dispatch;

import io.drakon.flightpath.IDispatcher;
import io.drakon.flightpath.dispatch.JavaDispatcher;
import io.drakon.flightpath.lib.BlackholeExceptionHandler;

public class JavaDispatcherTests extends GenericDispatcherTests {

    @Override
    protected IDispatcher getNewDispatcher() {
        return new JavaDispatcher(new BlackholeExceptionHandler());
    }

}
