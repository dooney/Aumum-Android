package com.aumum.app.mobile.core;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Cache;

import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;

/**
 * Created by Administrator on 19/10/2014.
 */
public class ImageLruCache implements Cache {
    final LinkedHashMap<String, Bitmap> map;
    private final int maxSize;

    private int size;
    private int putCount;
    private int evictionCount;
    private int hitCount;
    private int missCount;

    /** Create a cache using an appropriate portion of the available RAM as the maximum size. */
    public ImageLruCache(Context context) {
        this(calculateMemoryCacheSize(context));
    }

    /** Create a cache with a given maximum size in bytes. */
    public ImageLruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive.");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }

    private static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = getService(context, ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && SDK_INT >= HONEYCOMB) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
        }
        // Target ~15% of the available heap.
        return 1024 * 1024 * memoryClass / 7;
    }

    @Override
    public Bitmap get(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        Bitmap mapValue;
        synchronized (this) {
            mapValue = map.get(key);
            if (mapValue != null) {
                hitCount++;
                return mapValue;
            }
            missCount++;
        }

        return null;
    }

    private int getBitmapBytes(Bitmap bitmap) {
        int result;
        if (SDK_INT >= HONEYCOMB_MR1) {
            result = BitmapHoneycombMR1.getByteCount(bitmap);
        } else {
            result = bitmap.getRowBytes() * bitmap.getHeight();
        }
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + bitmap);
        }
        return result;
    }

    @Override
    public void set(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            throw new NullPointerException("key == null || bitmap == null");
        }

        Bitmap previous;
        synchronized (this) {
            putCount++;
            size += getBitmapBytes(bitmap);
            previous = map.put(key, bitmap);
            if (previous != null) {
                size -= getBitmapBytes(previous);
            }
        }

        trimToSize(maxSize);
    }

    private void trimToSize(int maxSize) {
        while (true) {
            String key;
            Bitmap value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(
                            getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) {
                    break;
                }

                Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= getBitmapBytes(value);
                evictionCount++;
            }
        }
    }

    /** Clear the cache. */
    public final void evictAll() {
        trimToSize(-1); // -1 will evict 0-sized elements
    }

    /** Returns the sum of the sizes of the entries in this cache. */
    public final synchronized int size() {
        return size;
    }

    /** Returns the maximum sum of the sizes of the entries in this cache. */
    public final synchronized int maxSize() {
        return maxSize;
    }

    public final synchronized void clear() {
        evictAll();
    }

    public final synchronized void clear(String key) {
        Bitmap value = map.get(key);
        map.remove(key);
        size -= getBitmapBytes(value);
        evictionCount++;
    }

    /** Returns the number of times {@link #get} returned a value. */
    public final synchronized int hitCount() {
        return hitCount;
    }

    /** Returns the number of times {@link #get} returned {@code null}. */
    public final synchronized int missCount() {
        return missCount;
    }

    /** Returns the number of times {@link #set(String, Bitmap)} was called. */
    public final synchronized int putCount() {
        return putCount;
    }

    /** Returns the number of values that have been evicted. */
    public final synchronized int evictionCount() {
        return evictionCount;
    }

    @TargetApi(HONEYCOMB)
    private static class ActivityManagerHoneycomb {
        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }

    @TargetApi(HONEYCOMB_MR1)
    private static class BitmapHoneycombMR1 {
        static int getByteCount(Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    }
}

