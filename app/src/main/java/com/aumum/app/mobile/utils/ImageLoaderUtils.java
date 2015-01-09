package com.aumum.app.mobile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Administrator on 5/11/2014.
 */
public class ImageLoaderUtils {

    public static void init(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCacheExtraOptions(480, 800)
                .diskCacheExtraOptions(480, 800, null)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static ImageLoader getInstance() {
        return ImageLoader.getInstance();
    }

    public static void displayImage(String imageUri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageUri, imageView);
    }
}
