package com.aumum.app.mobile.ui.moment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.MomentComment;

import java.util.List;

/**
 * Created by Administrator on 3/03/2015.
 */
public class MomentCommentsAdapter extends ArrayAdapter<MomentComment> {

    public MomentCommentsAdapter(Context context, List<MomentComment> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MomentCommentCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.moment_comment_listitem_inner, parent, false);
            card = new MomentCommentCard(convertView);
            convertView.setTag(card);
        } else {
            card = (MomentCommentCard) convertView.getTag();
        }

        MomentComment comment = getItem(position);
        card.refresh(comment);

        return convertView;
    }
}
