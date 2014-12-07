package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;

/**
 * Created by Administrator on 6/12/2014.
 */
public class VoiceMessageCard extends ChatMessageCard {

    private Activity activity;
    private View view;
    private TextView voiceLengthText;

    public VoiceMessageCard(Activity activity, View view) {
        super(activity, view);
        this.activity = activity;
        this.view = view;
        this.voiceLengthText = (TextView) view.findViewById(R.id.text_voice_length);
    }

    @Override
    public void refresh(EMConversation conversation, int position) {
        final EMMessage message = conversation.getMessage(position);
        VoiceMessageBody textBody = (VoiceMessageBody) message.getBody();
        voiceLengthText.setText(String.valueOf(textBody.getLength()) + "\"");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoicePlayClickListener.getInstance(activity).onPlay(message, view);
            }
        });

        super.refresh(conversation, position);
    }
}


