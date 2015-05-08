package com.aumum.app.mobile.core.service;

import java.io.File;

/**
 * Created by Administrator on 2/05/2015.
 */
public abstract class CloudStorageService {

    protected String directory;
    protected String baseUrl;

    public static interface UploadListener {
        public void onSuccess(String localUri);
        public void onFailure(Exception e);
    }

    public void init(String id) {
        directory = id;
    }

    protected String getFileName(String localUri) {
        return directory + "/" + Math.abs(localUri.hashCode());
    }

    protected String getRemoteUrl(String localUri) {
        return baseUrl + getFileName(localUri);
    }

    public abstract void uploadImage(String localUri,
                                     File file,
                                     UploadListener listener) throws Exception;

    public abstract String getThumbnail(String url);
}
