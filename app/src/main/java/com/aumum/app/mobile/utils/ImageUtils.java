package com.aumum.app.mobile.utils;

/**
 * Created by Administrator on 17/10/2014.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class ImageUtils {

    private static void closeOutputStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                Ln.d(e);
            }
        }
    }

    public static File createFile(Activity activity, byte[] bitmapData) throws IOException {
        String fileName = new Date().getTime() + ".jpg";
        File tempFile = new File(activity.getCacheDir(), fileName);
        tempFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        fileOutputStream.write(bitmapData);
        closeOutputStream(fileOutputStream);
        return tempFile;
    }

    public static byte[] getBytesBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            closeOutputStream(byteArrayOutputStream);
            return byteArray;
        }
        return null;
    }

    public static byte[] decodeBitmap(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 800 * 480);
        opts.inJustDecodeBounds = false;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inTempStorage = new byte[16 * 640];
        FileInputStream is = null;
        Bitmap bmp = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new FileInputStream(path);
            bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
            double scale = getScaling(opts.outWidth * opts.outHeight, 800 * 480);
            Bitmap bitmap;
            if (scale > 1) {
                bitmap = bmp;
            } else {
                bitmap = Bitmap.createScaledBitmap(bmp,
                    (int) (opts.outWidth * scale),
                    (int) (opts.outHeight * scale), true);
                bmp.recycle();
            }
            bitmap = rotate(getExifOrientation(path), bitmap);
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bitmap.recycle();
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
        return baos.toByteArray();
    }

    private static double getScaling(int src, int des) {
        double scale = Math.sqrt((double) des / (double) src);
        return scale;
    }

    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static int getExifOrientation(String imagePath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException ex) {
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = ExifInterface.ORIENTATION_ROTATE_90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = ExifInterface.ORIENTATION_ROTATE_180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = ExifInterface.ORIENTATION_ROTATE_270;
                        break;
                }

            }
        }
        return degree;
    }

    public static Bitmap rotate(int value, Bitmap bm) {
        if(bm == null) return null;
        Bitmap result = bm;

        if(value > 0) {
            Matrix matrix = getRotateMatrix(value);
            result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            if(bm != result){
                bm.recycle();
            }
        }
        return result;
    }

    private static Matrix getRotateMatrix(int ori){

        Matrix matrix = new Matrix();
        switch (ori) {
            case 0:
                matrix.setRotate(90);
                break;
            case 2:
                matrix.setScale(-1, 1);
                break;
            case 3:
                matrix.setRotate(180);
                break;
            case 4:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case 5:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case 6:
                matrix.setRotate(90);
                break;
            case 7:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case 8:
                matrix.setRotate(-90);
                break;
        }
        return matrix;
    }

    public static Bitmap getContactBitmapFromURI(Context context, String uri) {
        if (uri != null) {
            try {
                InputStream input = context.getContentResolver().openInputStream(Uri.parse(uri));
                if (input != null) {
                    return BitmapFactory.decodeStream(input);
                }
            } catch (FileNotFoundException e) {
                Ln.e(e);
            }
        }
        return null;
    }
}
