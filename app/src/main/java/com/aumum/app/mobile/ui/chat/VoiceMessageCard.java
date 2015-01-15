package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.DisplayUtils;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;

/**
 * Created by Administrator on 6/12/2014.
 */
public class VoiceMessageCard extends ChatMessageCard {

    private Activity activity;
    private View view;
    private View voiceLayout;
    private TextView voiceLengthText;
    private ImageView unreadImage;

    public VoiceMessageCard(Activity activity, View view) {
        super(activity, view);
        this.activity = activity;
        this.view = view;
        this.voiceLayout = view.findViewById(R.id.layout_voice);
        this.voiceLengthText = (TextView) view.findViewById(R.id.text_voice_length);
        this.unreadImage = (ImageView) view.findViewById(R.id.image_unread);
    }

    @Override
    public void refresh(final EMMessage message, boolean showTimestamp, int position) {
        VoiceMessageBody textBody = (VoiceMessageBody) message.getBody();
        int voiceLayoutWidth = 64 + 2 * textBody.getLength();
        if (voiceLayoutWidth > 240) {
            voiceLayoutWidth = 240;
        }
        voiceLayout.getLayoutParams().width = (DisplayUtils.dpToPx(activity, voiceLayoutWidth));
        voiceLengthText.setText(String.valueOf(textBody.getLength()) + "\"");

        if (unreadImage != null) {
            if (message.isListened()) {
                unreadImage.setVisibility(View.GONE);
            } else {
                unreadImage.setVisibility(View.VISIBLE);
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoicePlayClickListener.getInstance(activity).onPlay(message, view);
            }
        });

        super.refresh(message, showTimestamp, position);
    }
}


