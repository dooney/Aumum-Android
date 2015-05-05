package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Moment;

import java.util.List;

/**
 * Created by Administrator on 24/04/2015.
 */
public class MomentCardsAdapter extends ArrayAdapter<Moment> {

    public MomentCardsAdapter(Activity activity,
                              List<Moment> objects) {
        super(activity, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MomentCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.moment_listitem_inner, parent, false);
            card = new MomentCard(convertView);
            convertView.setTag(card);
        } else {
            card = (MomentCard) convertView.getTag();
        }

        Moment moment = getItem(position);
        card.refresh(moment);

        return convertView;
    }
}
