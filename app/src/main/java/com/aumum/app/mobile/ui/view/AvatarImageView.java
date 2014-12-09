package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 18/10/2014.
 */
public class AvatarImageView extends CircleImageView {
    private Context context;

    public AvatarImageView(Context context) {
        super(context);
        this.context = context;
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void getFromUrl(String imageUrl) {
        ImageLoaderUtils.getInstance().displayImage(imageUrl, this, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                AvatarImageView.this.setImageResource(R.drawable.ic_avatar);
                super.onLoadingStarted(imageUri, view);
            }
        });
    }
}
