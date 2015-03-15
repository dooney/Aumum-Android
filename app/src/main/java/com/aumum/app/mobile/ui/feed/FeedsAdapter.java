package com.aumum.app.mobile.ui.feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Feed;

import java.util.List;

/**
 * Created by Administrator on 15/03/2015.
 */
public class FeedsAdapter extends ArrayAdapter<Feed> {

    public FeedsAdapter(Context context, List<Feed> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FeedCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.feed_listitem_inner, parent, false);
            card = new FeedCard(convertView);
            convertView.setTag(card);
        } else {
            card = (FeedCard) convertView.getTag();
        }

        Feed feed = getItem(position);
        card.refresh(feed);

        return convertView;
    }
}
