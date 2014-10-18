package com.aumum.app.mobile.core;

/**
 * Created by Administrator on 17/10/2014.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.webkit.MimeTypeMap;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class ImageUtils {

    public static final int GALLERY_INTENT_CALLED = 1017;

    private static final String TEMP_FILE_NAME = "temp.png";

    private static final int ZERO_INT_VALUE = 0;

    private static final int ATTACH_WIDTH = 200;
    private static final int ATTACH_HEIGHT = 200;

    private static final int FULL_QUALITY = 100;

    private static final DisplayImageOptions UIL_DEFAULT_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisk(true).cacheInMemory(true).build();

    private static void closeOutputStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                Ln.d(e);
            }
        }
    }

    private static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static ImageLoaderConfiguration getImageLoaderConfiguration(Context context) {
        final int MEMORY_CACHE_LIMIT = 2 * 1024 * 1024;
        final int THREAD_POOL_SIZE = 5;
        final int COMPRESS_QUALITY = 60;
        final int MAX_IMAGE_WIDTH_FOR_MEMORY_CACHE = 600;
        final int MAX_IMAGE_HEIGHT_FOR_MEMORY_CACHE = 1200;

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(MAX_IMAGE_WIDTH_FOR_MEMORY_CACHE, MAX_IMAGE_HEIGHT_FOR_MEMORY_CACHE)
                .diskCacheExtraOptions(MAX_IMAGE_WIDTH_FOR_MEMORY_CACHE, MAX_IMAGE_HEIGHT_FOR_MEMORY_CACHE, null).threadPoolSize(THREAD_POOL_SIZE)
                .threadPriority(Thread.NORM_PRIORITY).denyCacheImageMultipleSizesInMemory().memoryCache(
                        new UsingFreqLimitedMemoryCache(MEMORY_CACHE_LIMIT)).writeDebugLogs()
                .defaultDisplayImageOptions(UIL_DEFAULT_DISPLAY_OPTIONS).imageDecoder(
                        new SmartUriDecoder(context, new BaseImageDecoder(false)))
                .denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(
                        new HashCodeFileNameGeneratorWithoutToken()).build();
        return imageLoaderConfiguration;
    }

    public static Bitmap getThumbnailFromVideo(String videoPath) {
        if (videoPath.contains("file://")) {
            videoPath = videoPath.replace("file://", "");
        }
        return ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
    }

    public static byte[] getBytesBitmap(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, FULL_QUALITY, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        closeOutputStream(byteArrayOutputStream);
        return byteArray;
    }

    public static Bitmap createScaledBitmap(Activity activity, Bitmap unscaledBitmap) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        int displayWidth = display.getWidth();

        Bitmap scaledBitmap = createScaledBitmap(unscaledBitmap, displayWidth, displayWidth, ScalingLogic.FIT);

        return scaledBitmap;
    }

    private static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
                                      ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth,
                dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth,
                dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    private static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                  ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, ZERO_INT_VALUE, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
                return new Rect(ZERO_INT_VALUE, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(ZERO_INT_VALUE, ZERO_INT_VALUE, srcWidth, srcHeight);
        }
    }

    private static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                 ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(ZERO_INT_VALUE, ZERO_INT_VALUE, dstWidth,
                        (int) (dstWidth / srcAspect));
            } else {
                return new Rect(ZERO_INT_VALUE, ZERO_INT_VALUE, (int) (dstHeight * srcAspect),
                        dstHeight);
            }
        } else {
            return new Rect(ZERO_INT_VALUE, ZERO_INT_VALUE, dstWidth, dstHeight);
        }
    }

    public static void getImage(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, GALLERY_INTENT_CALLED);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            activity.startActivityForResult(intent, GALLERY_INTENT_CALLED);
        }
    }

    public static void showFullImage(Context context, String absolutePath) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + absolutePath);
        intent.setDataAndType(uri, "image/*");
        context.startActivity(intent);
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap resultBitmap;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        float r;

        if (originalWidth > originalHeight) {
            resultBitmap = Bitmap.createBitmap(originalHeight, originalHeight, Bitmap.Config.ARGB_8888);
            r = originalHeight / 2;
        } else {
            resultBitmap = Bitmap.createBitmap(originalWidth, originalWidth, Bitmap.Config.ARGB_8888);
            r = originalWidth / 2;
        }

        Canvas canvas = new Canvas(resultBitmap);

        final Paint paint = new Paint();
        final Rect rect = new Rect(ZERO_INT_VALUE, ZERO_INT_VALUE, originalWidth, originalHeight);

        paint.setAntiAlias(true);
        canvas.drawARGB(ZERO_INT_VALUE, ZERO_INT_VALUE, ZERO_INT_VALUE, ZERO_INT_VALUE);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return resultBitmap;
    }

    public static String getAbsolutePathByBitmap(Activity activity, Bitmap origBitmap) {
        File tempFile = new File(activity.getExternalFilesDir(null), TEMP_FILE_NAME);
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            bos = new ByteArrayOutputStream();
            origBitmap.compress(Bitmap.CompressFormat.PNG, FULL_QUALITY, bos);
            byte[] bitmapData = bos.toByteArray();
            fos = new FileOutputStream(tempFile);
            fos.write(bitmapData);
            closeOutputStream(fos);
            closeOutputStream(bos);
        } catch (IOException e) {
            Toaster.showLong(activity, R.string.invalid_image);
        } finally {
            closeOutputStream(fos);
            closeOutputStream(bos);
        }
        return tempFile.getAbsolutePath();
    }

    public static File getFileFromBitmap(Activity activity, Bitmap origBitmap) throws IOException {
        int width = dipToPixels(activity, ATTACH_WIDTH);
        int height = dipToPixels(activity, ATTACH_HEIGHT);
        Bitmap bitmap = createScaledBitmap(origBitmap, width, height, ScalingLogic.FIT);
        byte[] bitmapData = getBytesBitmap(bitmap);
        File tempFile = createFile(activity, bitmapData);
        return tempFile;
    }

    public static File createFile(Activity activity, byte[] bitmapData) throws IOException {
        File tempFile = new File(activity.getCacheDir(), TEMP_FILE_NAME);
        tempFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        fileOutputStream.write(bitmapData);
        closeOutputStream(fileOutputStream);
        return tempFile;
    }

    public static Bitmap getBitmap(Activity activity, Uri originalUri) {
        BitmapFactory.Options bitmapOptions = getBitmapOption();
        Bitmap selectedBitmap = null;
        try {
            ParcelFileDescriptor descriptor = activity.getContentResolver().openFileDescriptor(originalUri, "r");
            selectedBitmap = BitmapFactory.decodeFileDescriptor(descriptor.getFileDescriptor(), null, bitmapOptions);
        } catch (FileNotFoundException e) {
            Toaster.showLong(activity, R.string.invalid_image);
        }
        return selectedBitmap;
    }

    private static BitmapFactory.Options getBitmapOption() {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inDither = false;
        bitmapOptions.inPurgeable = true;
        bitmapOptions.inInputShareable = true;
        bitmapOptions.inTempStorage = new byte[32 * 1024];
        return bitmapOptions;
    }

    private enum ScalingLogic {
        CROP, FIT
    }

    /*
    * TODO Sergey Fedunets: class will be realised for video attach
     */
    public static class SmartUriDecoder implements ImageDecoder {

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

    private static class HashCodeFileNameGeneratorWithoutToken extends HashCodeFileNameGenerator {

        private static final String FACEBOOK_PATTERN = "https://graph.facebook.com/";
        private static final String TOKEN_PATTERN = "\\?token+=+.*";

        @Override
        public String generate(String imageUri) {
            if (imageUri.contains(FACEBOOK_PATTERN)) {
                return imageUri;
            }
            String replace = imageUri.replaceAll(TOKEN_PATTERN, "");
            return super.generate(replace);
        }
    }
}
