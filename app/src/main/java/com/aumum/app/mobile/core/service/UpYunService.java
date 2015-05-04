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
    public boolean uploadImage(String localUri, File file) throws Exception {
        return upYun.writeFile(getFileName(localUri), file);
    }
}
