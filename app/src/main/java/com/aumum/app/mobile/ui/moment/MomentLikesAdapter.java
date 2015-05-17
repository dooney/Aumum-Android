package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.MomentLike;

import java.util.List;

/**
 * Created by Administrator on 17/05/2015.
 */
public class MomentLikesAdapter extends ArrayAdapter<MomentLike> {

    public MomentLikesAdapter(Activity activity,
                              List<MomentLike> likes) {
        super(activity, 0, likes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MomentLikeCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.moment_like_listitem_inner, parent, false);
            card = new MomentLikeCard(convertView);
            convertView.setTag(card);
        } else {
            card = (MomentLikeCard) convertView.getTag();
        }

        MomentLike momentLike = getItem(position);
        card.refresh(momentLike);

        return convertView;
    }
}
