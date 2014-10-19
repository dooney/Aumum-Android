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
    private Context context;
    private List<Comment> dataSet;

    public CommentsAdapter(Context context, List<Comment> objects) {
        super(context, 0, objects);
        this.context = context;
        dataSet = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CommentCard card;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.comments_listitem_inner, parent, false);
            card = new CommentCard(convertView);
            convertView.setTag(card);
        } else {
            card = (CommentCard) convertView.getTag();
        }

        final Comment comment = dataSet.get(position);
        card.updateView(comment);

        return convertView;
    }
}
