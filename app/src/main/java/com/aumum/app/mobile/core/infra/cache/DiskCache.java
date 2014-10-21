package com.aumum.app.mobile.core.infra.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 1/10/2014.
 */
public class DiskCache {
    private static DiskCache instance;

    private DiskLruCache diskLruCache;

    public static DiskCache getInstance(Context context, String path) {
        if (instance == null) {
            instance = new DiskCache(context, path);
        }
        return instance;
    }

    private DiskCache(Context context, String path) {
        try {
            File cacheDir = getDiskCacheDir(context, path);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            diskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public boolean hasKey(String key) {
        try {
            String hash = hashKeyForDisk(key);
            DiskLruCache.Snapshot snapShot = diskLruCache.get(hash);
            return snapShot != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void save(String key, Object data) {
        FileInputStream fileInputStream = null;
        try {
            String hash = hashKeyForDisk(key);
            DiskLruCache.Editor editor = diskLruCache.edit(hash);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                try {
                    objectOutputStream.writeObject(data);
                    editor.commit();
                } catch (IOException e) {
                    editor.abort();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Object get(String key) {
        FileInputStream fileInputStream = null;
        try {
            String hash = hashKeyForDisk(key);
            DiskLruCache.Snapshot snapShot = diskLruCache.get(hash);
            if (snapShot != null) {
                fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                return objectInputStream.readObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
