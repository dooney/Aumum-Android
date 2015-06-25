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
 * Created by Administrator on 20/06/2015.
 */
public class MomentGridAdapter extends ArrayAdapter<Moment> {

    private Activity activity;

    public MomentGridAdapter(Activity activity,
                             List<Moment> objects) {
        super(activity, 0, objects);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MomentGridCard card;

        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.moment_grid_listitem_inner, parent, false);
            card = new MomentGridCard(activity, convertView);
            convertView.setTag(card);
        } else {
            card = (MomentGridCard) convertView.getTag();
        }

        Moment moment = getItem(position);
        card.refresh(moment);

        return convertView;
    }
}
