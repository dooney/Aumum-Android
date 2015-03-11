package com.aumum.app.mobile.ui.saving;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.SavingComment;
import com.aumum.app.mobile.ui.saving.SavingCommentCard;

import java.util.List;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingCommentsAdapter extends ArrayAdapter<SavingComment> {

    public SavingCommentsAdapter(Context context, List<SavingComment> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SavingCommentCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.saving_comment_listitem_inner, parent, false);
            card = new SavingCommentCard(convertView);
            convertView.setTag(card);
        } else {
            card = (SavingCommentCard) convertView.getTag();
        }

        SavingComment comment = getItem(position);
        card.refresh(comment);

        return convertView;
    }
}
