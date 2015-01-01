package com.aumum.app.mobile.ui.party;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.PartyReason;

import java.util.List;

/**
 * Created by Administrator on 28/10/2014.
 */
public class ReasonsAdapter extends ArrayAdapter<PartyReason> {

    public ReasonsAdapter(Context context, List<PartyReason> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReasonCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.party_reason_listitem_inner, parent, false);
            card = new ReasonCard(convertView);
            convertView.setTag(card);
        } else {
            card = (ReasonCard) convertView.getTag();
        }

        PartyReason reason = getItem(position);
        card.refresh(reason);

        return convertView;
    }
}
