package com.aumum.app.mobile.utils;

/**
 * Created by Administrator on 6/12/2014.
 */
public class StorageUtils {

    public static boolean isExitsSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
