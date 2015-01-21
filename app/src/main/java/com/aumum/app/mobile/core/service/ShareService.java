package com.aumum.app.mobile.core.service;

import android.app.Activity;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.github.kevinsawicki.wishlist.Toaster;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
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
        if (title == null || title.isEmpty()) {
            Toaster.showShort(activity, R.string.error_title_is_null_for_sharing);
            return;
        }
        setSINAShareDetails(title, content, imageUrl);
        setWEIXINShareDetails(title, content, imageUrl);
        setCircleShareDetails(title, content, imageUrl);
        setQQShareDetails(title, content, imageUrl);

        controller.getConfig().setPlatformOrder(
                SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ);
        controller.getConfig().removePlatform(SHARE_MEDIA.TENCENT, SHARE_MEDIA.QZONE);
        controller.getConfig().closeToast();
        controller.openShare(activity, false);
    }

    private void setShareDetails(BaseShareContent details,
                                 String title,
                                 String content,
                                 String imageUrl) {
        details.setTitle(title);
        if (content != null && !content.isEmpty()) {
            details.setShareContent(content);
        } else {
            details.setShareContent(title);
        }
        if (imageUrl != null) {
            UMImage umImage = new UMImage(activity, imageUrl);
            details.setShareMedia(umImage);
        }
        details.setTargetUrl(Constants.Link.GOOGLE_PLAY_URL + Constants.APP_NAME);
        controller.setShareMedia(details);
    }

    private void setSINAShareDetails(String title, String content, String imageUrl) {
        SinaShareContent details = new SinaShareContent();
        setShareDetails(details, title, content, imageUrl);
    }

    private void setWEIXINShareDetails(String title, String content, String imageUrl) {
        WeiXinShareContent details = new WeiXinShareContent();
        setShareDetails(details, title, content, imageUrl);
    }

    private void setCircleShareDetails(String title, String content, String imageUrl) {
        CircleShareContent details = new CircleShareContent();
        setShareDetails(details, title, content, imageUrl);
    }

    private void setQQShareDetails(String title, String content, String imageUrl) {
        QQShareContent details = new QQShareContent();
        setShareDetails(details, title, content, imageUrl);
    }

    private void addSINAPlatform() {
        controller.getConfig().setSsoHandler(new SinaSsoHandler());
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
        String appId = "1104116792";
        String appKey = "laxdxbEPeIrELtht";
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId, appKey);
        qqSsoHandler.addToSocialSDK();
    }
}
