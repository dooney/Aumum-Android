package com.aumum.app.mobile.core.service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 14/11/2014.
 */
public class ScheduleService {

    private ScheduledThreadPoolExecutor exec;
    private OnScheduleListener onScheduleListener;
    private long initDelay;
    private long interval;

    public static interface OnScheduleListener {
        public void onAction();
    }

    public ScheduleService(final OnScheduleListener onScheduleListener,
                           final long initDelay,
                           final long interval) {
        this.onScheduleListener = onScheduleListener;
        this.initDelay = initDelay;
        this.interval = interval;
    }

    public void start() {
        if (onScheduleListener != null) {
            exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    onScheduleListener.onAction();
                }
            }, initDelay, interval, TimeUnit.MILLISECONDS);
        }
    }

    public void shutDown() {
        if (exec != null) {
            exec.shutdown();
            exec = null;
        }
    }
}
