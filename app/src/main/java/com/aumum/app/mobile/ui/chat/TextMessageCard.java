package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.events.DeleteChatMessageEvent;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 24/11/2014.
 */
public class TextMessageCard extends ChatMessageCard {

    private SpannableTextView textBodyText;

    public TextMessageCard(Activity activity,
                           Bus bus,
                           View view) {
        super(activity, bus, view);
        textBodyText = (SpannableTextView) view.findViewById(R.id.text_text_body);
    }

    @Override
    public void refresh(final EMMessage message, boolean showTimestamp, int position) {
        TextMessageBody textBody = (TextMessageBody) message.getBody();
        textBodyText.setSpannableText(textBody.getMessage());
        textBodyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionDialog(message);
            }
        });

        super.refresh(message, showTimestamp, position);
    }

    private void showActionDialog(final EMMessage message) {
        List<String> actions = new ArrayList<>();
        actions.add(activity.getString(R.string.label_copy));
        if (message.getFrom().equals(chatId)) {
            actions.add(activity.getString(R.string.label_delete));
        }
        new ListViewDialog(activity, null, actions,
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                copyText(message);
                                break;
                            case 1:
                                bus.post(new DeleteChatMessageEvent(message.getMsgId()));
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void copyText(EMMessage message) {
        TextMessageBody textBody = (TextMessageBody) message.getBody();
        ClipboardManager clipboard = (ClipboardManager) activity.
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Chat Message", textBody.getMessage());
        clipboard.setPrimaryClip(clip);
        Toaster.showShort(activity, R.string.info_chat_text_copied);
    }
}