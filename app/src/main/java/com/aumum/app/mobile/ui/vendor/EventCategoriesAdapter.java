package com.aumum.app.mobile.ui.vendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.EventCategory;

import java.util.List;

/**
 * Created by Administrator on 21/03/2015.
 */
public class EventCategoriesAdapter extends ArrayAdapter<EventCategory> {

    public EventCategoriesAdapter(Context context, List<EventCategory> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EventCategoryCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.event_category_listitem_inner, parent, false);
            card = new EventCategoryCard(convertView);
            convertView.setTag(card);
        } else {
            card = (EventCategoryCard) convertView.getTag();
        }

        EventCategory eventCategory = getItem(position);
        card.refresh(eventCategory);

        return convertView;
    }
}
