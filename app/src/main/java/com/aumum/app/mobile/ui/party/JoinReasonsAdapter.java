package com.aumum.app.mobile.ui.party;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.PartyJoinReason;

import java.util.List;

/**
 * Created by Administrator on 28/10/2014.
 */
public class JoinReasonsAdapter extends ArrayAdapter<PartyJoinReason> {
    private Context context;
    private List<PartyJoinReason> dataSet;

    public JoinReasonsAdapter(Context context, List<PartyJoinReason> objects) {
        super(context, 0, objects);
        this.context = context;
        this.dataSet = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final JoinReasonCard card;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.party_join_reason_listitem_inner, parent, false);
            card = new JoinReasonCard(convertView);
            convertView.setTag(card);
        } else {
            card = (JoinReasonCard) convertView.getTag();
        }

        PartyJoinReason reason = dataSet.get(position);
        card.refresh(reason);

        return convertView;
    }
}
