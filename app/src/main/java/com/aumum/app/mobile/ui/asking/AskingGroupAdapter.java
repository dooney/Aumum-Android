package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingGroup;

import java.util.List;

/**
 * Created by Administrator on 31/03/2015.
 */
public class AskingGroupAdapter extends ArrayAdapter<AskingGroup> {

    private Activity activity;
    private AskingGroupQuitListener listener;

    public AskingGroupAdapter(Activity activity,
                              List<AskingGroup> objects,
                              AskingGroupQuitListener listener) {
        super(activity, 0, objects);
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AskingGroupCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.asking_group_listitem_inner, parent, false);
            card = new AskingGroupCard(activity, convertView, listener);
            convertView.setTag(card);
        } else {
            card = (AskingGroupCard) convertView.getTag();
        }

        AskingGroup askingGroup = getItem(position);
        card.refresh(askingGroup);

        return convertView;
    }
}
