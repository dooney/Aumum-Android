package com.aumum.app.mobile.core.service;

import java.io.File;

import main.java.com.UpYun;

/**
 * Created by Administrator on 10/12/2014.
 */
public class UpYunService extends CloudStorageService {

    private UpYun upYun;

    @Override
    public void init(String id) {
        super.init(id);
        baseUrl = "http://aumum.b0.upaiyun.com/";
        upYun = new UpYun("aumum", "admin", "vIyDLeFgc80MeKjZObSi");
        upYun.setDebug(true);
        upYun.setApiDomain(UpYun.ED_AUTO);
    }

    @Override
    public void uploadImage(String localUri,
                            File file,
                            UploadListener listener) throws Exception {
        if (!upYun.writeFile(getFileName(localUri), file)) {
            throw new Exception("图片上传失败，请重试");
        }
    }

    @Override
    public String getThumbnail(String url) {
        return null;
    }
}
