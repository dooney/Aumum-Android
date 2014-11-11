package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;

import java.util.Date;

/**
 * Created by Administrator on 11/11/2014.
 */
public class ChatMessageCard implements SendMessageListener.OnActionListener {

    private SendMessageListener listener;

    private Activity activity;
    private TextView timeStampText;
    private TextView userNameText;
    private TextView textBodyText;
    private ProgressBar progressBar;

    public ChatMessageCard(Activity activity, View view) {
        this.activity = activity;
        timeStampText = (TextView) view.findViewById(R.id.text_time_stamp);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        textBodyText = (TextView) view.findViewById(R.id.text_text_body);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        listener = new SendMessageListener();
        listener.setListener(this);
    }

    public void refresh(EMConversation conversation, int position) {
        EMMessage message = conversation.getMessage(position);
        if (position == 0) {
            timeStampText.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timeStampText.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (DateUtils.isCloseEnough(message.getMsgTime(), conversation.getMessage(position - 1).getMsgTime())) {
                timeStampText.setVisibility(View.GONE);
            } else {
                timeStampText.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timeStampText.setVisibility(View.VISIBLE);
            }
        }

        userNameText.setText(message.getFrom());

        TextMessageBody textBody = (TextMessageBody) message.getBody();
        textBodyText.setText(textBody.getMessage());

        if (message.direct == EMMessage.Direct.SEND ) {
            switch (message.status) {
                case INPROGRESS:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    break;
                case FAIL:
                    progressBar.setVisibility(View.GONE);
                    break;
                default:
                    progressBar.setVisibility(View.VISIBLE);
                    listener.sendMessage(message);
            }
        }
    }

    @Override
    public void onSuccess() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onError(int code, String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onProgress(int progress, String status) {

    }
}
