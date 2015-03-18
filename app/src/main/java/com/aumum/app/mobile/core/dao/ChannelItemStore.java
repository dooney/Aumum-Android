package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.ChannelItem;
import com.aumum.app.mobile.utils.FeedItemUtils;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14/03/2015.
 */
public class ChannelItemStore {

    private RSSReader rssReader;

    public ChannelItemStore(RSSReader rssReader) {
        this.rssReader = rssReader;
    }

    public List<ChannelItem> getUpwardsList(String uri) throws Exception {
        ArrayList<ChannelItem> channelItems = new ArrayList<>();
        RSSFeed feed = rssReader.load(uri);
        for (RSSItem item: feed.getItems()) {
            String content = FeedItemUtils.improveHtmlContent(item.getDescription(), "");
            ArrayList<String> images = FeedItemUtils.getImageURLs(content);
            String text = FeedItemUtils.getTextFromHtml(item.getDescription());
            ChannelItem channelItem = new ChannelItem(text, images);
            channelItems.add(channelItem);
        }
        return channelItems;
    }
}
