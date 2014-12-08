package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;

/**
 * Created by Administrator on 8/12/2014.
 */
public class ImageMessageCard extends ChatMessageCard {

    private ImageView sentImage;

    public ImageMessageCard(Activity activity, View view) {
        super(activity, view);
        this.sentImage = (ImageView) view.findViewById(R.id.image_sent);
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
        ImageLoaderUtils.displayImage(imagePath, sentImage);

        super.refresh(conversation, position);
    }
}
