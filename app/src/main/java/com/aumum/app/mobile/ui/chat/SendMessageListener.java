package com.aumum.app.mobile.ui.chat;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.service.ChatService;
import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage;

import javax.inject.Inject;

/**
 * Created by Administrator on 11/11/2014.
 */
public class SendMessageListener {

    @Inject ChatService chatService;

    private OnActionListener listener;

    public static interface OnActionListener {
        public void onSuccess();
        public void onError(int code, String message);
        public void onProgress(int progress, String status);
    }

    public void setListener(OnActionListener listener) {
        this.listener = listener;
    }

    public SendMessageListener() {
        Injector.inject(this);
    }

    public void sendMessage(EMMessage message) {
        chatService.sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                if (listener != null) {
                    listener.onError(code, message);
                }
            }

            @Override
            public void onProgress(int progress, String status) {
                if (listener != null) {
                    listener.onProgress(progress, status);
                }
            }
        });
    }
}
