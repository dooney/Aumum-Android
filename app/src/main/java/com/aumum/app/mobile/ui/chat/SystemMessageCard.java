package com.aumum.app.mobile.ui.chat;

import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.ChatMessage;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;

import java.util.Date;

/**
 * Created by Administrator on 24/11/2014.
 */
public class SystemMessageCard implements ChatMessageListener {

    private TextView timeStampText;
    private SpannableTextView systemMsgText;

    public SystemMessageCard(View view) {
        timeStampText = (TextView) view.findViewById(R.id.text_time_stamp);
        systemMsgText = (SpannableTextView) view.findViewById(R.id.text_system_msg);
    }

    @Override
    public void refresh(ChatMessage chatMessage, boolean showTimestamp, int position) {
        final EMMessage message = chatMessage.getMessage();
        if (position == 0) {
            timeStampText.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timeStampText.setVisibility(View.VISIBLE);
        } else {
            if (showTimestamp) {
                timeStampText.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timeStampText.setVisibility(View.VISIBLE);
            } else {
                timeStampText.setVisibility(View.GONE);
            }
        }

        TextMessageBody textBody = (TextMessageBody) message.getBody();
        systemMsgText.setSpannableText(textBody.getMessage());
    }
}
