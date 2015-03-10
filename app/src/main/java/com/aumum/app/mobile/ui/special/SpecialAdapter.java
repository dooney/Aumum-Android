package com.aumum.app.mobile.ui.special;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Special;

import java.util.List;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialAdapter extends ArrayAdapter<Special> {

    public SpecialAdapter(Context context, List<Special> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SpecialCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.special_griditem_inner, parent, false);
            card = new SpecialCard(convertView);
            convertView.setTag(card);
        } else {
            card = (SpecialCard) convertView.getTag();
        }

        Special special = getItem(position);
        card.refresh(special);

        return convertView;
    }
}
