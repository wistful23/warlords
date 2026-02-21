package com.def.warlords.util;

/**
 * @author wistful23
 * @version 1.23
 */
public class Timer {

    private static java.util.Timer timer;

    private final Runnable listener;

    private java.util.TimerTask timerTask;

    public Timer(Runnable listener) {
        this.listener = listener;
    }

    public static void release() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void start(int delay) {
        stop();
        timerTask = new java.util.TimerTask() {
            @Override
            public void run() {
                listener.run();
            }
        };
        if (timer == null) {
            timer = new java.util.Timer();
        }
        timer.schedule(timerTask, delay, delay);
    }

    public void stop() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
