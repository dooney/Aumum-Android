package com.aumum.app.mobile.core.service;

import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.UpYunUtils;

/**
 * Created by Administrator on 18/10/2014.
 */
public class FileUploadService {
    private OnFileUploadListener onFileUploadListener;

    public void setOnFileUploadListener(OnFileUploadListener onFileUploadListener) {
        this.onFileUploadListener = onFileUploadListener;
    }

    public static interface OnFileUploadListener {
        public void onUploadSuccess(String fileUrl);
        public void onUploadFailure(Exception e);
    }

    public void upload(final String fileName, final byte[] data) {
        new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (!UpYunUtils.uploadImage(fileName, data)) {
                    throw new Exception("网络不给力，请检查");
                }
                return true;
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                if (onFileUploadListener != null) {
                    onFileUploadListener.onUploadSuccess(UpYunUtils.getImageFullPath(fileName));
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
