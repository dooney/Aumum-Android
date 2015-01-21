package com.aumum.app.mobile.core.service;

import android.app.Activity;

import com.aumum.app.mobile.core.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by Administrator on 12/12/2014.
 */
public class ShareService {

    private final Activity activity;
    private final UMSocialService controller;

    public ShareService(Activity activity) {
        this.activity = activity;
        this.controller = UMServiceFactory.getUMSocialService(Constants.APP_NAME);

        addSINAPlatform();
        addWEXINPlatform();
        addQQPlatform();
        Log.LOG = true;
    }

    public void show(String title, String content, String imageUrl) {
        setWEIXINShareDetails(title, content, imageUrl);
        setCircleShareDetails(title, content, imageUrl);

        controller.getConfig().setPlatformOrder(
                SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ);
        controller.getConfig().removePlatform(SHARE_MEDIA.TENCENT, SHARE_MEDIA.QZONE);
        controller.getConfig().closeToast();
        controller.openShare(activity, false);
    }

    private void addSINAPlatform() {
        controller.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    private void setWEIXINShareDetails(String title, String content, String imageUrl) {
        WeiXinShareContent details = new WeiXinShareContent();
        details.setShareContent(content);
        details.setTitle(title);
        if (imageUrl != null) {
            UMImage umImage = new UMImage(activity, imageUrl);
            details.setShareMedia(umImage);
        }
        details.setTargetUrl(Constants.Link.GOOGLE_PLAY_URL + Constants.APP_NAME);
        controller.setShareMedia(details);
    }

    private void setCircleShareDetails(String title, String content, String imageUrl) {
        CircleShareContent details = new CircleShareContent();
        details.setShareContent(content);
        details.setTitle(title);
        if (imageUrl != null) {
            UMImage umImage = new UMImage(activity, imageUrl);
            details.setShareMedia(umImage);
        }
        details.setTargetUrl(Constants.Link.GOOGLE_PLAY_URL + Constants.APP_NAME);
        controller.setShareMedia(details);
    }

    private void addWEXINPlatform() {
        String appId = "wx494dd3ca31ee1e1c";
        String appSecret = "b1a5adccc0ed5592177e0c67ea22dfa0";
        UMWXHandler wxHandler = new UMWXHandler(activity, appId, appSecret);
        wxHandler.showCompressToast(false);
        wxHandler.addToSocialSDK();

        UMWXHandler wxCircleHandler = new UMWXHandler(activity, appId, appSecret);
        wxCircleHandler.showCompressToast(false);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    private void addQQPlatform() {
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId, appKey);
        qqSsoHandler.addToSocialSDK();
    }
}
