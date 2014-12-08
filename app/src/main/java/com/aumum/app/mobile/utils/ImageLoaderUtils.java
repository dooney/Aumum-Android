package com.aumum.app.mobile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 5/11/2014.
 */
public class ImageLoaderUtils {

    public interface ImageLoadingListener {
        public void onLoadingStarted();
        public void onLoadingFailed();
        public void onLoadingComplete();
        public void onLoadingCancelled();
    }

    public static void init(Context context) {
        ImageLoader.getInstance().init(getImageLoaderConfiguration(context));
    }

    private static final DisplayImageOptions UIL_DEFAULT_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.image_placeholder)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .build();

    private static ImageLoaderConfiguration getImageLoaderConfiguration(Context context) {
        final int MEMORY_CACHE_LIMIT = 2 * 1024 * 1024;
        final int THREAD_POOL_SIZE = 5;
        final int MAX_IMAGE_WIDTH_FOR_MEMORY_CACHE = 600;
        final int MAX_IMAGE_HEIGHT_FOR_MEMORY_CACHE = 1200;

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(MAX_IMAGE_WIDTH_FOR_MEMORY_CACHE, MAX_IMAGE_HEIGHT_FOR_MEMORY_CACHE)
                .diskCacheExtraOptions(MAX_IMAGE_WIDTH_FOR_MEMORY_CACHE, MAX_IMAGE_HEIGHT_FOR_MEMORY_CACHE, null)
                .threadPoolSize(THREAD_POOL_SIZE)
                .threadPriority(Thread.NORM_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(MEMORY_CACHE_LIMIT))
                .writeDebugLogs()
                .defaultDisplayImageOptions(UIL_DEFAULT_DISPLAY_OPTIONS)
                .imageDecoder(new SmartUriDecoder(context, new BaseImageDecoder(false)))
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        return imageLoaderConfiguration;
    }

    private static Bitmap getThumbnailFromVideo(String videoPath) {
        if (videoPath.contains("file://")) {
            videoPath = videoPath.replace("file://", "");
        }
        return ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
    }

    private static class SmartUriDecoder implements ImageDecoder {

        private final BaseImageDecoder imageUriDecoder;

        private final Reference<Context> context;

        public SmartUriDecoder(Context context, BaseImageDecoder imageUriDecoder) {
            if (imageUriDecoder == null) {
                throw new NullPointerException("Image decoder can't be null");
            }

            this.context = new WeakReference(context);
            this.imageUriDecoder = imageUriDecoder;
        }

        @Override
        public Bitmap decode(ImageDecodingInfo info) throws IOException {
            if (TextUtils.isEmpty(info.getImageKey())) {
                return null;
            }

            String cleanedUriString = cleanUriString(info.getImageKey());
            if (isVideoUri(cleanedUriString)) {
                return makeVideoThumbnail(info.getTargetSize().getWidth(), info.getTargetSize().getHeight(),
                        cleanedUriString);
            } else {
                return imageUriDecoder.decode(info);
            }
        }

        private Bitmap makeVideoThumbnail(int width, int height, String filePath) {
            if (filePath == null) {
                return null;
            }
            Bitmap thumbnail = getThumbnailFromVideo(filePath);
            if (thumbnail == null) {
                return null;
            }

            Bitmap scaledThumb = scaleBitmap(thumbnail, width, height);
            thumbnail.recycle();

            addVideoIcon(scaledThumb);
            return scaledThumb;
        }

        private void addVideoIcon(Bitmap source) {
            Canvas canvas = new Canvas(source);
            Bitmap icon = BitmapFactory.decodeResource(context.get().getResources(), R.drawable.ic_fa_video_camera);

            float left = (source.getWidth() / 2) - (icon.getWidth() / 2);
            float top = (source.getHeight() / 2) - (icon.getHeight() / 2);

            canvas.drawBitmap(icon, left, top, null);
        }

        private boolean isVideoUri(String uri) {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

            return mimeType == null ? false : mimeType.startsWith("video/");
        }

        private Bitmap scaleBitmap(Bitmap origBitmap, int width, int height) {
            float scale = Math.min(((float) width) / ((float) origBitmap.getWidth()),
                    ((float) height) / ((float) origBitmap.getHeight()));
            return Bitmap.createScaledBitmap(origBitmap, (int) (((float) origBitmap.getWidth()) * scale),
                    (int) (((float) origBitmap.getHeight()) * scale), false);
        }

        private String cleanUriString(String contentUriWithAppendedSize) {
            return contentUriWithAppendedSize.replaceFirst("_\\d+x\\d+$", "");
        }
    }

    public static void displayImage(String imageUri, ImageView imageView, final ImageLoadingListener listener) {
        ImageLoader.getInstance().displayImage(imageUri, imageView,
                new com.nostra13.universalimageloader.core.listener.ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (listener != null) {
                    listener.onLoadingStarted();
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (listener != null) {
                    listener.onLoadingFailed();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (listener != null) {
                    listener.onLoadingComplete();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (listener != null) {
                    listener.onLoadingCancelled();
                }
            }
        });
    }

    public static void displayImage(String imageUri, ImageView imageView) {
        displayImage(imageUri, imageView, null);
    }

    public static void displayImage(int resId, ImageView imageView) {
        String imageUri = "drawable://" + resId;
        displayImage(imageUri, imageView, null);
    }
}
