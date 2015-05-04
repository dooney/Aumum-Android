package com.aumum.app.mobile.core.service;

import java.io.File;

/**
 * Created by Administrator on 2/05/2015.
 */
public abstract class CloudStorageService {

    protected String directory;
    protected String baseUrl;

    public void init(String id) {
        directory = id;
    }

    protected String getFileName(String localUri) {
        return directory + "/" + Math.abs(localUri.hashCode());
    }

    protected String getRemoteUrl(String localUri) {
        return baseUrl + getFileName(localUri);
    }

    public abstract boolean uploadImage(String localUri, File file) throws Exception;
}
