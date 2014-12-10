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
        SafeAsyncTask<Boolean> uploadTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                boolean result = UpYunUtils.uploadImage(fileName, data);
                if (!result) {
                    throw new Exception();
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
        };
        uploadTask.execute();
    }
}
