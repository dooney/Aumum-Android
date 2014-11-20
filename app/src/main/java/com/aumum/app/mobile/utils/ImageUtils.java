package com.aumum.app.mobile.utils;

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
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

import com.aumum.app.mobile.R;
import com.github.kevinsawicki.wishlist.Toaster;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageUtils {

    public static final int GALLERY_INTENT_CALLED = 1017;

    private static final String TEMP_FILE_NAME = "temp.png";

    private static final int ZERO_INT_VALUE = 0;

    private static final int ATTACH_WIDTH = 200;
    private static final int ATTACH_HEIGHT = 200;

    private static final int FULL_QUALITY = 100;

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

    public static byte[] getBytesBitmap(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (imageBitmap != null) {
            imageBitmap.compress(Bitmap.CompressFormat.PNG, FULL_QUALITY, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            closeOutputStream(byteArrayOutputStream);
            return byteArray;
        }
        return null;
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
            Toaster.showLong(activity, R.string.error_load_image);
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
}
