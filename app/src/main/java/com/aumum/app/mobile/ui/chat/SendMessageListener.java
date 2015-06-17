package com.aumum.app.mobile.ui.chat;

import com.aumum.app.mobile.utils.EMChatUtils;
import com.easemob.EMCallBack;
import com.easemob.chat.EMMessage;

/**
 * Created by Administrator on 11/11/2014.
 */
public class SendMessageListener {

    private OnActionListener listener;

    public interface OnActionListener {
        void onSuccess();
        void onError(int code, String message);
        void onProgress(int progress, String status);
    }

    public void setListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void sendMessage(EMMessage message) {
        EMChatUtils.sendMessage(message, new EMCallBack() {
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
