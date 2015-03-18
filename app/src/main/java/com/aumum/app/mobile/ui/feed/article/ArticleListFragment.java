package com.aumum.app.mobile.ui.feed.article;

import android.content.Intent;

import com.aumum.app.mobile.core.model.Feed;
import com.aumum.app.mobile.ui.feed.FeedListFragment;

/**
 * Created by Administrator on 18/03/2015.
 */
public class ArticleListFragment extends FeedListFragment {

    public ArticleListFragment() {
        type = TYPE_ARTICLE;
    }

    @Override
    protected void startFeedActivity(Feed feed) {
        final Intent intent = new Intent(getActivity(), ArticleActivity.class);
        intent.putExtra(ArticleActivity.INTENT_TITLE, feed.getScreenName());
        intent.putExtra(ArticleActivity.INTENT_URI, feed.getUri());
        startActivity(intent);
    }
}
