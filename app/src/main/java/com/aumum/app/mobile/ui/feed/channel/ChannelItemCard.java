package com.aumum.app.mobile.ui.feed.channel;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.ChannelItem;
import com.aumum.app.mobile.ui.image.ImageCard;
import com.aumum.app.mobile.ui.view.SpannableTextView;

/**
 * Created by Administrator on 14/03/2015.
 */
public class ChannelItemCard extends ImageCard {

    private ChannelItem channelItem;

    public ChannelItemCard(Activity activity, ChannelItem channelItem) {
        super(activity, R.layout.channel_item_listitem_inner, channelItem.getImages());
        this.activity = activity;
        this.channelItem = channelItem;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        SpannableTextView contentText = (SpannableTextView) view.findViewById(R.id.text_content);
        if (channelItem.getText() != null) {
            contentText.setSpannableText(channelItem.getText());
            contentText.setVisibility(View.VISIBLE);
        } else {
            contentText.setVisibility(View.GONE);
        }
    }
}
