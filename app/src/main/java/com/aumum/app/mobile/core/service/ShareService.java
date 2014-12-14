package com.aumum.app.mobile.core.service;

import android.app.Activity;

import com.aumum.app.mobile.core.Constants;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.facebook.controller.UMFacebookHandler;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.whatsapp.controller.UMWhatsAppHandler;

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
        addFacebookPlatform();
        addWhatsAppPlatform();
        addEmailPlatform();
        addSmsPlatform();
    }

    public void show(Activity activity) {
        controller.getConfig().setPlatformOrder(
                SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ,
                SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.WHATSAPP, SHARE_MEDIA.EMAIL, SHARE_MEDIA.SMS);
        controller.getConfig().removePlatform( SHARE_MEDIA.TENCENT, SHARE_MEDIA.QZONE);
        controller.openShare(activity, false);
    }

    private void addSINAPlatform() {

    }

    private void addWEXINPlatform() {
        String appId = "wx967daebe835fbeac";
        String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
        UMWXHandler wxHandler = new UMWXHandler(activity, appId, appSecret);
        wxHandler.addToSocialSDK();

        UMWXHandler wxCircleHandler = new UMWXHandler(activity, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    private void addQQPlatform() {
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId, appKey);
        qqSsoHandler.addToSocialSDK();
    }

    private void addFacebookPlatform() {
        UMFacebookHandler mFacebookHandler = new UMFacebookHandler(activity,
                "567261760019884", UMFacebookHandler.PostType.FEED);
        mFacebookHandler.addToSocialSDK();
    }

    private void addWhatsAppPlatform() {
        UMWhatsAppHandler whatsAppHandler = new UMWhatsAppHandler(activity);
        whatsAppHandler.addToSocialSDK();
    }

    private void addEmailPlatform() {
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();
    }

    private void addSmsPlatform() {
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }
}