package com.aumum.app.mobile.core;

import com.aumum.app.mobile.events.FollowEvent;
import com.aumum.app.mobile.events.JoinEvent;
import com.aumum.app.mobile.events.LikeEvent;
import com.aumum.app.mobile.events.MessageEvent;
import com.aumum.app.mobile.events.PushNotificationEvent;
import com.aumum.app.mobile.util.Ln;
import com.aumum.app.mobile.util.SafeAsyncTask;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageHandler {
    private Bus bus;
    private BootstrapService service;

    private SafeAsyncTask<Boolean> followTask;
    private SafeAsyncTask<Boolean> joinTask;
    private SafeAsyncTask<Boolean> likeTask;

    private final String NOTIFICATION_TEXT = "你有新的消息";

    public MessageHandler(Bus bus, BootstrapService bootstrapService) {
        this.bus = bus;
        this.bus.register(this);
        service = bootstrapService;
    }

    private void process(final MessageEvent event) {
        Message message = new Message();
        message.setType(event.getMessageType());
        message.setFromUserId(event.getFromUserId());
        message.setToUserId(event.getToUserId());
        message = service.newMessage(message);
        service.addUserMessage(event.getToUserId(), message.getObjectId());

        bus.post(new PushNotificationEvent(event.getToUserId(), NOTIFICATION_TEXT));
    }

    @Subscribe
    public void onFollowEvent(final FollowEvent event) {
        if (followTask != null) {
            return;
        }

        followTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                process(event);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                followTask = null;
            }
        };
        followTask.execute();
    }

    @Subscribe
    public void onJoinEvent(final JoinEvent event) {
        if (joinTask != null) {
            return;
        }

        joinTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                process(event);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                joinTask = null;
            }
        };
        joinTask.execute();
    }

    @Subscribe
    public void onLikeEvent(final LikeEvent event) {
        if (likeTask != null) {
            return;
        }

        likeTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                process(event);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                likeTask = null;
            }
        };
        likeTask.execute();
    }
}
