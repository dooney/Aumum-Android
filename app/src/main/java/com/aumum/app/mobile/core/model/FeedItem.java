package com.aumum.app.mobile.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14/03/2015.
 */
public class FeedItem {

    private String text;
    private ArrayList<String> images = new ArrayList<>();

    public FeedItem(String text, List<String> images) {
        this.text = text;
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getImages() {
        return images;
    }
}
