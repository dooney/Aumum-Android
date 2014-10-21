package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.events.MessageEvent;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.NotificationUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageListener {
    private RestService service;

    private SafeAsyncTask<Boolean> task;

    public MessageListener(RestService restService) {
        service = restService;
    }

    private void process(final MessageEvent event) {
        Message message = new Message();
        message.setType(event.getType());
        message.setFromUserId(event.getFromUserId());
        message.setToUserId(event.getToUserId());
        message = service.newMessage(message);
        service.addUserMessage(event.getToUserId(), message.getObjectId());

        NotificationUtils.pushNotification(event.getToUserId());
    }

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
