package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.FeedItem;
import com.aumum.app.mobile.utils.FeedItemUtils;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14/03/2015.
 */
public class FeedItemStore {

    private RSSReader rssReader;

    public FeedItemStore(RSSReader rssReader) {
        this.rssReader = rssReader;
    }

    public List<FeedItem> getUpwardsList(String uri) throws Exception {
        ArrayList<FeedItem> feedItems = new ArrayList<>();
        RSSFeed feed = rssReader.load(uri);
        for (RSSItem item: feed.getItems()) {
            String content = FeedItemUtils.improveHtmlContent(item.getDescription(), "");
            ArrayList<String> images = FeedItemUtils.getImageURLs(content);
            String text = FeedItemUtils.getTextFromHtml(item.getDescription());
            FeedItem feedItem = new FeedItem(text, images);
            feedItems.add(feedItem);
        }
        return feedItems;
    }
}
