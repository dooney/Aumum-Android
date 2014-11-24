package com.aumum.app.mobile.ui.chat;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

/**
 * Created by Administrator on 24/11/2014.
 */
public class SystemMessageCard implements CardRefreshListener {

    private TextView systemMsgText;

    public SystemMessageCard(View view) {
        systemMsgText = (TextView) view.findViewById(R.id.text_system_msg);
    }

    @Override
    public void refresh(EMConversation conversation, int position) {
        EMMessage message = conversation.getMessage(position);
        TextMessageBody textBody = (TextMessageBody) message.getBody();
        systemMsgText.setText(textBody.getMessage());
    }
}
