package com.consultpal.android.providers;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by ines on 5/14/16.
 */
public class BusProvider {

    private static final Bus REST_BUS = new Bus(ThreadEnforcer.ANY);
    private static final Bus UI_BUS = new Bus();

    private BusProvider() {}

    /**
     * The REST bus runs on any available thread.
     */
    public static Bus getRestBusInstance() {
        return REST_BUS;
    }

    /**
     * The UI bus runs on the default thread, the UI thread.
     */
    public static Bus getUIBusInstance () {
        return UI_BUS;
    }

}
