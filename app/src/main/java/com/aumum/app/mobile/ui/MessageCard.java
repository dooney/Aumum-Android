package com.aumum.app.mobile.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Message;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 8/10/2014.
 */
public class MessageCard extends Card {
    private Message message;

    public MessageCard(Context context, Message message) {
        super(context, R.layout.message_listitem_inner);
        this.message = message;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        TextView body = (TextView)view.findViewById(R.id.text_body);
        body.setText(message.getBody());
    }
}
