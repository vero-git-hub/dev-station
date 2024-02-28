package com.dev.station.manager.monitoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class TimerManager {
    private static List<Timer> timers = new ArrayList<>();

    public static synchronized void addTimer(Timer timer) {
        timers.add(timer);
    }

    public static synchronized void removeTimer(Timer timer) {
        timer.cancel();
        timers.remove(timer);
    }

    public static void stopAll() {
        for (Timer timer : timers) {
            timer.cancel();
        }
        timers.clear();
    }
}
