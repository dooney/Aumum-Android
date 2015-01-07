package com.aumum.app.mobile.ui.party;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.PartyComment;

import java.util.List;

/**
 * Created by Administrator on 13/10/2014.
 */
public class PartyCommentsAdapter extends ArrayAdapter<PartyComment> {

    public PartyCommentsAdapter(Context context, List<PartyComment> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PartyCommentCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.party_comment_listitem_inner, parent, false);
            card = new PartyCommentCard(convertView);
            convertView.setTag(card);
        } else {
            card = (PartyCommentCard) convertView.getTag();
        }

        PartyComment comment = getItem(position);
        card.refresh(comment);

        return convertView;
    }
}
