package com.aumum.app.mobile;

import android.content.Context;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.events.PushNotificationEvent;
import com.aumum.app.mobile.events.SubscribeChannelEvent;
import com.aumum.app.mobile.events.UnSubscribeChannelEvent;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Administrator on 8/10/2014.
 */
public class ParseModule {
    private static ParseModule instance;

    @Inject Bus bus;

    public static ParseModule getInstance() {
        if (instance == null) {
            instance = new ParseModule();
        }
        return instance;
    }

    private ParseModule() {
        Injector.inject(this);
        bus.register(this);
    }

    public void init(Context context) {
        Parse.initialize(context, Constants.Http.PARSE_APP_ID, Constants.Http.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    @Subscribe
    public void onSubscribeChannelEvent(SubscribeChannelEvent subscribeChannelEvent) {
        ParsePush.subscribeInBackground(subscribeChannelEvent.getChannel());
    }

    @Subscribe
    public void onUnSubscribeChannelEvent(UnSubscribeChannelEvent unSubscribeChannelEvent) {
        ParsePush.unsubscribeInBackground(unSubscribeChannelEvent.getChannel());
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        ParsePush push = new ParsePush();
        push.setChannel(pushNotificationEvent.getChannel());
        push.setMessage(pushNotificationEvent.getMessage());
        push.sendInBackground();
    }
}
