package com.def.warlords.util;

/**
 * @author wistful23
 * @version 1.23
 */
public class Toggle {

    private boolean on;

    public Toggle(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    public boolean isOff() {
        return !on;
    }

    public void turnOn() {
        on = true;
    }

    public void toggle() {
        on = !on;
    }
}
