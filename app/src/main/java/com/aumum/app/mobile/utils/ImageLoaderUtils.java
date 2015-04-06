package com.aumum.app.mobile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

/**
 * Created by Administrator on 5/11/2014.
 */
public class ImageLoaderUtils {

    public static void init(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheSize(200 * 1024 * 1024)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static void displayImage(String imageUri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageUri, imageView);
    }

    public static void displayImage(String imageUri,
                                    final ImageView imageView,
                                    final int placeHolderResId) {
        ImageLoader.getInstance().displayImage(imageUri, imageView,
                new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                imageView.setImageResource(placeHolderResId);
                super.onLoadingStarted(imageUri, view);
            }
        });
    }

    public static Bitmap loadImage(String imageUri) {
        return ImageLoader.getInstance().loadImageSync(imageUri);
    }

    public static void putInMemory(String imageUri, Bitmap bitmap) {
        ImageLoader.getInstance().getMemoryCache().put(imageUri, bitmap);
    }

    public static AbsListView.OnScrollListener getOnScrollListener() {
        return new PauseOnScrollListener(ImageLoader.getInstance(), true, true);
    }

    public static File getFile(String imageUri) {
        return ImageLoader.getInstance().getDiskCache().get(imageUri);
    }
}
