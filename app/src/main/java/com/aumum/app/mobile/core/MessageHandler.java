package com.aumum.app.mobile.core;

import com.aumum.app.mobile.events.FollowEvent;
import com.aumum.app.mobile.util.Ln;
import com.aumum.app.mobile.util.SafeAsyncTask;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageHandler {
    private BootstrapService service;

    private SafeAsyncTask<Boolean> task;

    public MessageHandler(Bus bus, BootstrapService bootstrapService) {
        bus.register(this);
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
                message.setBody(event.getMessage());
                message = service.newMessage(message);
                User user = event.getFollowedUser();
                service.addUserMessage(user.getObjectId(), message.getObjectId());
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
