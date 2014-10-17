package com.aumum.app.mobile.core;

/**
 * Created by Administrator on 17/10/2014.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.aumum.app.mobile.utils.Ln;

import java.io.File;
import java.io.IOException;

public class ReceiveUriScaledBitmapTask extends AsyncTask<Object, Uri, Uri> {

    private Activity activity;
    private ReceiveUriScaledBitmapListener receiveUriScaledBitmapListener;

    public ReceiveUriScaledBitmapTask(Activity activity, ReceiveUriScaledBitmapListener receiveUriScaledBitmapListener) {
        this.activity = activity;
        this.receiveUriScaledBitmapListener = receiveUriScaledBitmapListener;
    }

    @Override
    protected Uri doInBackground(Object[] params) {
        Uri originalUri = (Uri) params[0];

        File bitmapFile = null;
        Uri outputUri = null;

        Bitmap bitmap = ImageUtils.getBitmap(activity, originalUri);
        Bitmap scaledBitmap = ImageUtils.createScaledBitmap(activity, bitmap);

        try {
            bitmapFile = ImageUtils.getFileFromBitmap(activity, scaledBitmap);
        } catch (IOException error) {
            Ln.d(error);
        }

        if (bitmapFile != null) {
            outputUri = Uri.fromFile(bitmapFile);
        }

        return outputUri;
    }

    @Override
    protected void onPostExecute(Uri uri) {
        receiveUriScaledBitmapListener.onUriScaledBitmapReceived(uri);
    }

    public interface ReceiveUriScaledBitmapListener {

        public void onUriScaledBitmapReceived(Uri uri);
    }
}
