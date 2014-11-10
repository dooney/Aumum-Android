package com.aumum.app.mobile.ui.circle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Conversation;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ConversationCard {

    private ImageView avatarImage;
    private TextView screenNameText;

    public ConversationCard(View view) {
        this.avatarImage = (ImageView) view.findViewById(R.id.image_avatar);
        this.screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
    }

    public void refresh(Conversation conversation) {
        screenNameText.setText(conversation.getScreenName());
    }
}
