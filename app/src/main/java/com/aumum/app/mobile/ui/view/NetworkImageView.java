package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 18/10/2014.
 */
public class NetworkImageView extends ImageView {
    private Context context;

    public NetworkImageView(Context context) {
        super(context);
        this.context = context;
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void getFromUrl(String imageUrl, int placeholderResource) {
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(placeholderResource)
                .into(this);
    }
}
