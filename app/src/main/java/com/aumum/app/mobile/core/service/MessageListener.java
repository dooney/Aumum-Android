package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.events.MessageEvent;
import com.aumum.app.mobile.events.PushNotificationEvent;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageListener {
    private Bus bus;
    private RestService service;

    private SafeAsyncTask<Boolean> task;

    private final String NOTIFICATION_TEXT = "您有新的消息";

    public MessageListener(Bus bus, RestService restService) {
        this.bus = bus;
        this.bus.register(this);
        service = restService;
    }

    private void process(final MessageEvent event) {
        Message message = new Message();
        message.setType(event.getType());
        message.setFromUserId(event.getFromUserId());
        message.setToUserId(event.getToUserId());
        message = service.newMessage(message);
        service.addUserMessage(event.getToUserId(), message.getObjectId());

        bus.post(new PushNotificationEvent(event.getToUserId(), NOTIFICATION_TEXT));
    }

    @Subscribe
    public void onMessageEvent(final MessageEvent event) {
        if (task != null) {
            return;
        }

        if (!event.getToUserId().equals(event.getFromUserId())) {
            task = new SafeAsyncTask<Boolean>() {
                public Boolean call() throws Exception {
                    process(event);
                    return true;
                }

                @Override
                protected void onException(final Exception e) throws RuntimeException {
                    if (!(e instanceof RetrofitError)) {
                        final Throwable cause = e.getCause() != null ? e.getCause() : e;
                        if (cause != null) {
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
        }
    }
}
