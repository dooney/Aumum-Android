package com.aumum.app.mobile.core;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

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

    public void upload(String fileName, byte[] data) {
        final ParseFile file = new ParseFile(fileName, data);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (onFileUploadListener != null) {
                        onFileUploadListener.onUploadSuccess(file.getUrl());
                    }
                } else {
                    if (onFileUploadListener != null) {
                        onFileUploadListener.onUploadFailure(e);
                    }
                }
            }
        });
    }
}
