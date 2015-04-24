package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.events.DeleteChatMessageEvent;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/12/2014.
 */
public class ImageMessageCard extends ChatMessageCard {

    private ImageView image;
    private TextView progressText;

    public ImageMessageCard(Activity activity,
                            Bus bus,
                            View view) {
        super(activity, bus, view);
        this.image = (ImageView) view.findViewById(R.id.image_body);
        this.progressText = (TextView) view.findViewById(R.id.text_progress);
    }

    @Override
    public void refresh(final EMMessage message, boolean showTimestamp, int position) {
        ImageMessageBody imageBody = (ImageMessageBody) message.getBody();

        final String imageUri;
        if (message.direct == EMMessage.Direct.RECEIVE) {
            imageUri = imageBody.getRemoteUrl();
        } else {
            imageUri = "file:/" + imageBody.getLocalUrl();
        }
        ImageLoaderUtils.displayImage(imageUri, image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageUrl = imageUri;
                final Intent intent = new Intent(activity, ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
                activity.startActivity(intent);
            }
        });
        if (message.getFrom().equals(chatId)) {
            image.setLongClickable(true);
            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showActionDialog(message);
                    return false;
                }
            });
        }

        super.refresh(message, showTimestamp, position);
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
                progressText.setVisibility(View.VISIBLE);
                progressText.setText(String.format("%,d%%", progress));
            }
        });
    }

    private void showActionDialog(final EMMessage message) {
        List<String> actions = new ArrayList<>();
        actions.add(activity.getString(R.string.label_delete));
        new ListViewDialog(activity, null, actions,
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                bus.post(new DeleteChatMessageEvent(message.getMsgId()));
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }
}