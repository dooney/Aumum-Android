package com.aumum.app.mobile.utils;

/**
 * Created by Administrator on 17/10/2014.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static byte[] getBytesBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    Ln.e(e);
                }
            }
            return byteArray;
        }
        return null;
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
