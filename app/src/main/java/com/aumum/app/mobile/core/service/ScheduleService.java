package com.aumum.app.mobile.core.service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 14/11/2014.
 */
public class ScheduleService {

    private ScheduledThreadPoolExecutor exec;
    private OnScheduleListener onScheduleListener;
    private final long INIT_DELAY_IN_MILLISECONDS = 60000;
    private final long INTERVAL_IN_MILLISECONDS = 60000;

    public static interface OnScheduleListener {
        public void onAction();
    }

    public ScheduleService(final OnScheduleListener onScheduleListener) {
        this.onScheduleListener = onScheduleListener;
    }

    public void start() {
        if (onScheduleListener != null) {
            exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    onScheduleListener.onAction();
                }
            }, INIT_DELAY_IN_MILLISECONDS, INTERVAL_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
        }
    }

    public void shutDown() {
        if (exec != null) {
            exec.shutdown();
            exec = null;
        }
    }
}
