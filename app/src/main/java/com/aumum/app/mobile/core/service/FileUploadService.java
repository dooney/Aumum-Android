package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.utils.SafeAsyncTask;

import java.io.File;

/**
 * Created by Administrator on 18/10/2014.
 */
public class FileUploadService {
    private CloudStorageService cloudStorageService;
    private OnFileUploadListener onFileUploadListener;

    public void setOnFileUploadListener(OnFileUploadListener onFileUploadListener) {
        this.onFileUploadListener = onFileUploadListener;
    }

    public static interface OnFileUploadListener {
        public void onUploadSuccess(String remoteUrl);
        public void onUploadFailure(Exception e);
    }

    public FileUploadService(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    public void init(String id) {
        cloudStorageService.init(id);
    }

    public void upload(final String localUri, final File file) {
        new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (!cloudStorageService.uploadImage(localUri, file)) {
                    throw new Exception("文件上传失败，请重试");
                }
                return true;
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                if (onFileUploadListener != null) {
                    String remoteUrl = cloudStorageService.getRemoteUrl(localUri);
                    onFileUploadListener.onUploadSuccess(remoteUrl);
                }
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if (onFileUploadListener != null) {
                    onFileUploadListener.onUploadFailure(e);
                }
            }
        }.execute();
    }
}
