package com.aumum.app.mobile.ui.asking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.AskingReply;

import java.util.List;

/**
 * Created by Administrator on 30/11/2014.
 */
public class AskingRepliesAdapter extends ArrayAdapter<AskingReply> {

    private Context context;
    private Asking asking;

    public AskingRepliesAdapter(Context context, List<AskingReply> objects, Asking asking) {
        super(context, 0, objects);
        this.context = context;
        this.asking = asking;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AskingReplyCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.asking_reply_listitem_inner, parent, false);
            card = new AskingReplyCard(context, convertView, asking);
            convertView.setTag(card);
        } else {
            card = (AskingReplyCard) convertView.getTag();
        }

        AskingReply askingReply = getItem(position);
        card.refresh(askingReply);

        return convertView;
    }
}
