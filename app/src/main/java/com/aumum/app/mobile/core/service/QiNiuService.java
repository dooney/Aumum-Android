package com.aumum.app.mobile.core.service;

import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.File;

/**
 * Created by Administrator on 2/05/2015.
 */
public class QiNiuService extends CloudStorageService {

    private final String ACCESS_KEY = "bP2yEOI2QzgppmJ6tzXphUr2W6CdUq6CuKKr6cp3";
    private final String SECRET_KEY = "e5iufo1CZ9Pg34ZA5I88TPwA_BR5VlDVniWBRKes";
    private String token;
    private UploadManager uploadManager;

    @Override
    public void init(String id) {
        super.init(id);
        baseUrl = "http://aumum.qiniudn.com/";
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        token = auth.uploadToken("aumum");
        uploadManager = new UploadManager();
    }

    private static String getKey(String filePath) {
        Integer hashCode = Math.abs(filePath.hashCode());
        return hashCode.toString();
    }

    @Override
    public boolean uploadImage(String filePath, File file) throws Exception {
        Response response = uploadManager.put(file, getKey(filePath), token);
        return response.isOK();
    }
}
