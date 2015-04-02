package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingGroup;

import java.util.List;

/**
 * Created by Administrator on 2/04/2015.
 */
public class AskingRecommendGroupAdapter extends ArrayAdapter<AskingGroup> {

    private Activity activity;
    private AskingRecommendGroupJoinListener listener;

    public AskingRecommendGroupAdapter(Activity activity,
                                       List<AskingGroup> objects,
                                       AskingRecommendGroupJoinListener listener) {
        super(activity, 0, objects);
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AskingRecommendGroupCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(
                    R.layout.asking_recommend_group_listitem_inner, parent, false);
            card = new AskingRecommendGroupCard(activity, convertView, listener);
            convertView.setTag(card);
        } else {
            card = (AskingRecommendGroupCard) convertView.getTag();
        }

        AskingGroup askingGroup = getItem(position);
        card.refresh(askingGroup);

        return convertView;
    }
}
