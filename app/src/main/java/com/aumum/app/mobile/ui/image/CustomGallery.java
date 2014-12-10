package com.aumum.app.mobile.ui.image;

/**
 * Created by Administrator on 8/12/2014.
 */
public class CustomGallery {
    public static final int FILE = 0;
    public static final int HTTP = 1;

    public int type;
    public String imageUri;
    public boolean isSelected;

    public String getUri() {
        switch (type) {
            case FILE:
                return "file://" + imageUri;
            case HTTP:
            default:
                return imageUri;
        }
    }
}
