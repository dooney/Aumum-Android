package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 7/10/2014.
 */
public class MessageDeliveryService {
    private RestService restService;

    private SafeAsyncTask<Boolean> task;

    public MessageDeliveryService(RestService restService) {
        this.restService = restService;
    }

    public void send(final Message message) {
        if (task != null) {
            return;
        }

        if (!message.getToUserId().equals(message.getFromUserId())) {
            task = new SafeAsyncTask<Boolean>() {
                public Boolean call() throws Exception {
                    Message response = restService.newMessage(message);
                    restService.addUserMessage(message.getToUserId(), response.getObjectId());
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
