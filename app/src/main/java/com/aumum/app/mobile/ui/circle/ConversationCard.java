package com.aumum.app.mobile.ui.circle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;

import java.util.Date;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ConversationCard {

    private ImageView avatarImage;
    private TextView screenNameText;
    private TextView timeStampText;
    private TextView messageBodyText;

    public ConversationCard(View view) {
        avatarImage = (ImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        timeStampText = (TextView) view.findViewById(R.id.text_time_stamp);
        messageBodyText = (TextView) view.findViewById(R.id.text_message_body);
    }

    public void refresh(EMContact contact, EMConversation conversation) {
        screenNameText.setText(contact.getNick());

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            timeStampText.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));

            TextMessageBody messageBody = (TextMessageBody) lastMessage.getBody();
            messageBodyText.setText(messageBody.getMessage());
        }
    }
}
