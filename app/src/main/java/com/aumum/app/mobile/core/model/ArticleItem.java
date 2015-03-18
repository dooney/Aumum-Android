package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 18/03/2015.
 */
public class ArticleItem {

    private String title;
    private String link;

    public ArticleItem(String title,
                       String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
