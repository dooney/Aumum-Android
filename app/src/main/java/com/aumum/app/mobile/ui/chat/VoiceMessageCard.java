package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.events.DeleteChatMessageEvent;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.DisplayUtils;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 6/12/2014.
 */
public class VoiceMessageCard extends ChatMessageCard {

    private View view;
    private View voiceLayout;
    private TextView voiceLengthText;
    private ImageView unreadImage;

    public VoiceMessageCard(Activity activity,
                            Bus bus,
                            View view) {
        super(activity, bus, view);
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
        if (message.getFrom().equals(chatId)) {
            view.setLongClickable(true);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showActionDialog(message);
                    return false;
                }
            });
        }

        super.refresh(message, showTimestamp, position);
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


