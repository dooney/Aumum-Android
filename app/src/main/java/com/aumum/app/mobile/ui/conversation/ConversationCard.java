package com.aumum.app.mobile.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Conversation;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;

import java.util.Date;

/**
 * Created by Administrator on 10/11/2014.
 */
public class ConversationCard {

    private Context context;
    private View view;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView timeStampText;
    private TextView messageBodyText;
    private ImageView unreadImage;

    private String id;
    private String screenName;
    private int type;

    public ConversationCard(Context context, View view) {
        this.context = context;
        this.view = view;

        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        timeStampText = (TextView) view.findViewById(R.id.text_time_stamp);
        messageBodyText = (TextView) view.findViewById(R.id.text_message_body);
        unreadImage = (ImageView) view.findViewById(R.id.image_unread);
    }

    public void refresh(Conversation conversation) {
        if (conversation.getContact() != null) {
            id = conversation.getContact().getChatId();
            screenName = conversation.getContact().getScreenName();
            avatarImage.getFromUrl(conversation.getContact().getAvatarUrl());
            type = ChatActivity.TYPE_SINGLE;
        } else if (conversation.getGroup() != null) {
            id = conversation.getGroup().getChatId();
            screenName = conversation.getGroup().getScreenName();
            avatarImage.getFromUrl(conversation.getGroup().getAvatarUrl());
            type = ChatActivity.TYPE_GROUP;
        }
        this.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(ChatActivity.INTENT_TITLE, screenName);
                intent.putExtra(ChatActivity.INTENT_TYPE, type);
                intent.putExtra(ChatActivity.INTENT_ID, id);
                context.startActivity(intent);
            }
        });
        screenNameText.setText(screenName);

        if (conversation.getEmConversation().getMsgCount() != 0) {
            EMMessage lastMessage = conversation.getEmConversation().getLastMessage();
            timeStampText.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));

            if (lastMessage.getType() == EMMessage.Type.TXT) {
                TextMessageBody messageBody = (TextMessageBody) lastMessage.getBody();
                messageBodyText.setText(messageBody.getMessage());
            } else if (lastMessage.getType() == EMMessage.Type.VOICE) {
                messageBodyText.setText("[" + context.getString(R.string.label_voice) + "]");
            } else if (lastMessage.getType() == EMMessage.Type.IMAGE) {
                messageBodyText.setText("[" + context.getString(R.string.label_image) + "]");
            }
        }

        if (conversation.getEmConversation().getUnreadMsgCount() > 0) {
            unreadImage.setVisibility(View.VISIBLE);
        } else {
            unreadImage.setVisibility(View.GONE);
        }
    }
}
