package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.MomentComment;

import java.util.List;

/**
 * Created by Administrator on 18/05/2015.
 */
public class MomentCommentsAdapter extends ArrayAdapter<MomentComment> {

    public MomentCommentsAdapter(Activity activity,
                                 List<MomentComment> likes) {
        super(activity, 0, likes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MomentCommentCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(
                    R.layout.moment_comment_listitem_inner, parent, false);
            card = new MomentCommentCard(convertView);
            convertView.setTag(card);
        } else {
            card = (MomentCommentCard) convertView.getTag();
        }

        MomentComment momentComment = getItem(position);
        card.refresh(momentComment);

        return convertView;
    }
}
