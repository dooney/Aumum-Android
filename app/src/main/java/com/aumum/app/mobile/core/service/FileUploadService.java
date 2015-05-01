package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.UpYunUtils;

import java.io.File;

/**
 * Created by Administrator on 18/10/2014.
 */
public class FileUploadService {
    private OnFileUploadListener onFileUploadListener;

    public void setOnFileUploadListener(OnFileUploadListener onFileUploadListener) {
        this.onFileUploadListener = onFileUploadListener;
    }

    public static interface OnFileUploadListener {
        public void onUploadSuccess(String remoteUrl);
        public void onUploadFailure(Exception e);
    }

    public void upload(final String localUri, final File file) {
        new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (!UpYunUtils.uploadImage(localUri, file)) {
                    throw new Exception("网络不给力，请检查");
                }
                return true;
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                if (onFileUploadListener != null) {
                    String remoteUrl = UpYunUtils.getImageFullPath(localUri);
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
