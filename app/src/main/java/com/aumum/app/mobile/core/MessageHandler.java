package com.aumum.app.mobile.core;

import com.aumum.app.mobile.events.FollowEvent;
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

    private SafeAsyncTask<Boolean> task;

    public MessageHandler(Bus bus, BootstrapService bootstrapService) {
        this.bus = bus;
        this.bus.register(this);
        service = bootstrapService;
    }

    @Subscribe
    public void onFollowEvent(final FollowEvent event) {
        if (task != null) {
            return;
        }

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                Message message = new Message();
                message.setType(event.getMessageType());
                message.setFromUserId(event.getFollowingUserId());
                message = service.newMessage(message);
                service.addUserMessage(event.getFollowedUserId(), message.getObjectId());

                bus.post(new PushNotificationEvent(event.getFollowedUserId(), Constants.NOTIFICATION_TEXT));

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
                task = null;
            }
        };
        task.execute();
        return;
    }
}
