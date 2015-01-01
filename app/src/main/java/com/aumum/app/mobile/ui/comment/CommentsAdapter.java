package com.aumum.app.mobile.ui.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Comment;

import java.util.List;

/**
 * Created by Administrator on 13/10/2014.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    public CommentsAdapter(Context context, List<Comment> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CommentCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.comment_listitem_inner, parent, false);
            card = new CommentCard(convertView);
            convertView.setTag(card);
        } else {
            card = (CommentCard) convertView.getTag();
        }

        Comment comment = getItem(position);
        card.refresh(comment);

        return convertView;
    }
}
