package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;

/**
 * Created by Administrator on 8/12/2014.
 */
public class ImageMessageCard extends ChatMessageCard {

    private Activity activity;
    private ImageView image;
    private TextView progressText;

    public ImageMessageCard(Activity activity, View view) {
        super(activity, view);
        this.activity = activity;
        this.image = (ImageView) view.findViewById(R.id.image_body);
        this.progressText = (TextView) view.findViewById(R.id.text_progress);
    }

    @Override
    public void refresh(EMConversation conversation, int position) {
        EMMessage message = conversation.getMessage(position);
        ImageMessageBody imageBody = (ImageMessageBody) message.getBody();

        String imagePath;
        if (message.direct == EMMessage.Direct.RECEIVE) {
            imagePath = imageBody.getRemoteUrl();
        } else {
            imagePath = "file:/" + imageBody.getLocalUrl();
        }
        ImageLoaderUtils.displayImage(imagePath, image);

        super.refresh(conversation, position);
    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressText.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onError(int code, String message) {
        super.onError(code, message);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressText.setVisibility(View.GONE);
            }
        });
    }

        @Override
    public void onProgress(final int progress, String status) {
        super.onProgress(progress, status);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressText.setText(String.format("%,d%%", progress));
            }
        });
    }

    @Override
    protected void sendMessage(EMMessage message) {
        progressText.setVisibility(View.VISIBLE);
        message.progress = 0;
        super.sendMessage(message);
    }
}