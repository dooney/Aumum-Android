package com.aumum.app.mobile.core.service;

import java.io.File;

/**
 * Created by Administrator on 18/10/2014.
 */
public class FileUploadService implements CloudStorageService.UploadListener {
    private CloudStorageService cloudStorageService;
    private FileUploadListener fileUploadListener;

    public void setFileUploadListener(FileUploadListener fileUploadListener) {
        this.fileUploadListener = fileUploadListener;
    }

    public static interface FileUploadListener {
        public void onUploadSuccess(String remoteUrl);
        public void onUploadFailure(Exception e);
    }

    public FileUploadService(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    public void init(String id) {
        cloudStorageService.init(id);
    }

    public void upload(final String localUri) throws Exception {
        File file = new File(localUri);
        if (!file.exists()) {
            throw new Exception("无效的文件地址");
        }
        cloudStorageService.uploadImage(localUri, file, this);
    }

    public String getThumbnail(String url) {
        return cloudStorageService.getThumbnail(url);
    }

    @Override
    public void onSuccess(String localUri) {
        if (fileUploadListener != null) {
            String remoteUrl = cloudStorageService.getRemoteUrl(localUri);
            fileUploadListener.onUploadSuccess(remoteUrl);
        }
    }

    @Override
    public void onFailure(Exception e) {
        if (fileUploadListener != null) {
            fileUploadListener.onUploadFailure(e);
        }
    }
}
