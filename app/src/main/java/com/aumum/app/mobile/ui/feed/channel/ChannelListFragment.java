package com.aumum.app.mobile.ui.feed.channel;

import android.content.Intent;

import com.aumum.app.mobile.core.model.Feed;
import com.aumum.app.mobile.ui.feed.FeedListFragment;

/**
 * Created by Administrator on 18/03/2015.
 */
public class ChannelListFragment extends FeedListFragment {

    public ChannelListFragment() {
        type = TYPE_CHANNEL;
    }

    @Override
    protected void startFeedActivity(Feed feed) {
        final Intent intent = new Intent(getActivity(), ChannelActivity.class);
        intent.putExtra(ChannelActivity.INTENT_TITLE, feed.getScreenName());
        intent.putExtra(ChannelActivity.INTENT_URI, feed.getUri());
        startActivity(intent);
    }
}
