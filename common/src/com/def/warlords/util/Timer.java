package com.def.warlords.util;

/**
 * @author wistful23
 * @version 1.23
 */
public class Timer {

    private static final java.util.Timer timer = new java.util.Timer();

    private final Runnable listener;

    private java.util.TimerTask timerTask;

    public Timer(Runnable listener) {
        this.listener = listener;
    }

    public void start(int delay) {
        stop();
        timerTask = new java.util.TimerTask() {
            @Override
            public void run() {
                listener.run();
            }
        };
        timer.schedule(timerTask, delay, delay);
    }

    public void stop() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
