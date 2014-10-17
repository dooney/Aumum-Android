package com.aumum.app.mobile.events;

import retrofit.RetrofitError;

/**
 * The event that is posted when a network error event occurs.
 */
public class NetworkErrorEvent {
    private RetrofitError cause;

    public NetworkErrorEvent(RetrofitError cause) {
        this.cause = cause;
    }

    public RetrofitError getCause() {
        return cause;
    }
}
