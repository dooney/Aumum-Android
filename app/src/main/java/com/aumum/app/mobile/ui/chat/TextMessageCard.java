package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

/**
 * Created by Administrator on 24/11/2014.
 */
public class TextMessageCard extends ChatMessageCard {

    private SpannableTextView textBodyText;

    public TextMessageCard(Activity activity, View view) {
        super(activity, view);
        textBodyText = (SpannableTextView) view.findViewById(R.id.text_text_body);
    }

    @Override
    public void refresh(EMConversation conversation, int position) {
        EMMessage message = conversation.getMessage(position);
        TextMessageBody textBody = (TextMessageBody) message.getBody();
        textBodyText.setSpannableText(textBody.getMessage());

        super.refresh(conversation, position);
    }
}