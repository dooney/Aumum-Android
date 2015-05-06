package com.aumum.app.mobile.core.service;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONObject;

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

    @Override
    public void uploadImage(final String localUri,
                            File file,
                            final UploadListener listener) throws Exception {
        uploadManager.put(file, getFileName(localUri), token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        if (info.isOK()) {
                            listener.onSuccess(localUri);
                        } else {
                            listener.onFailure(new Exception(info.error));
                        }
                    }
                }, null);
    }
}