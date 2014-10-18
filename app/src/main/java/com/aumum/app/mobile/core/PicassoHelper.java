package com.aumum.app.mobile.core;

import android.net.Uri;

import com.aumum.app.mobile.BootstrapApplication;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by Administrator on 18/10/2014.
 */
public class PicassoHelper {
    private static Picasso picasso = null;
    private static ImageLruCache lruCache = null;

    private static ImageLruCache getCache()
    {
        if (lruCache == null)
            lruCache = new ImageLruCache(BootstrapApplication.getInstance());
        return lruCache;
    }

    public static Picasso getPicasso()
    {
        if (picasso == null)
            picasso = new Picasso.Builder(BootstrapApplication.getInstance()).memoryCache(getCache()).build();
        return picasso;
    }

    public static void clearCache(String imageUrl) {
        String key = getKey(Uri.parse(imageUrl));
        getCache().clear(key);
    }

    private static final int KEY_PADDING = 50; // Determined by exact science.

    private static String getKey(Uri uri)
    {
        return getKey(uri, null, 0, 0, false, false, null);
    }

    private static String getKey(Uri uri, Integer resourceId, int targetWidth, int targetHeight, boolean centerCrop, boolean centerInside, List<Transformation> transformations)
    {
        StringBuilder builder = new StringBuilder();
        if (uri != null)
        {
            String path = uri.toString();
            builder.ensureCapacity(path.length() + KEY_PADDING);
            builder.append(path);
        }
        else
        {
            builder.ensureCapacity(KEY_PADDING);
            builder.append(resourceId);
        }
        builder.append('\n');

        if (targetWidth != 0)
        {
            builder.append("resize:").append(targetWidth).append('x').append(targetHeight);
            builder.append('\n');
        }
        if (centerCrop)
        {
            builder.append("centerCrop\n");
        }
        else if (centerInside)
        {
            builder.append("centerInside\n");
        }

        if (transformations != null)
        {
            for (int i = 0, count = transformations.size(); i < count; i++)
            {
                builder.append(transformations.get(i).key());
                builder.append('\n');
            }
        }

        return builder.toString();
    }
}