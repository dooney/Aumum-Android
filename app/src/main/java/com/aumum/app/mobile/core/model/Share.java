package com.aumum.app.mobile.core.model;

/**
 * Created by Administrator on 6/05/2015.
 */
public class Share {

    private String title;
    private String content;
    private String imageUrl;

    public Share(String title,
                 String content,
                 String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
