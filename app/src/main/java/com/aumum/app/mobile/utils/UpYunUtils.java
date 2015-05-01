package com.aumum.app.mobile.utils;

import java.io.File;

import main.java.com.UpYun;

/**
 * Created by Administrator on 10/12/2014.
 */
public class UpYunUtils {

    private static UpYun upYun;
    private static String directory;

    public static void init() {
        if (upYun == null) {
            upYun = new UpYun("aumum", "admin", "vIyDLeFgc80MeKjZObSi");
            upYun.setDebug(true);
            upYun.setApiDomain(UpYun.ED_AUTO);
        }
    }

    public static void setCurrentDir(String dir) {
        directory = dir;
    }

    private static String getImageRemotePath(String imagePath) {
        return directory + "/" + Math.abs(imagePath.hashCode()) + ".jpg";
    }

    public static boolean uploadImage(String filePath, File file) throws Exception {
        return upYun.writeFile(getImageRemotePath(filePath), file);
    }

    public static String getImageFullPath(String imagePath) {
        return "http://aumum.b0.upaiyun.com/" + getImageRemotePath(imagePath);
    }

    public static String getThumbnailUrl(String imageUrl) {
        return imageUrl + "!thumbnail";
    }
}
