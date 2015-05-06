package com.aumum.app.mobile.utils;

import android.app.Activity;
import android.content.Intent;

import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Share;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.BaseShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by Administrator on 12/12/2014.
 */
public class ShareUtils {

    private final static UMSocialService controller =
            UMServiceFactory.getUMSocialService(Constants.APP_NAME);

    public static void init(Activity activity) {
        addSINAPlatform(activity);
        addWEXINPlatform(activity);
        addQQPlatform(activity);
        Log.LOG = true;
    }

    public static void registerSSOCallback(int requestCode, int resultCode, Intent data) {
        UMSsoHandler ssoHandler = controller.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public static void show(Activity activity, Share share) {
        setSINAShareDetails(activity, share);
        setWEIXINShareDetails(activity, share);
        setCircleShareDetails(activity, share);
        setQQShareDetails(activity, share);

        controller.getConfig().setPlatformOrder(
                SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ);
        controller.getConfig().removePlatform(SHARE_MEDIA.TENCENT, SHARE_MEDIA.QZONE);
        controller.getConfig().closeToast();
        controller.openShare(activity, false);
    }

    private static void setShareDetails(Activity activity,
                                        BaseShareContent details,
                                        Share share) {
        String title = share.getTitle();
        details.setTitle(title);
        String content = share.getContent();
        if (content != null && !content.isEmpty()) {
            details.setShareContent(content);
        } else {
            details.setShareContent(title);
        }
        String imageUrl = share.getImageUrl();
        if (imageUrl != null) {
            UMImage umImage = new UMImage(activity, imageUrl);
            details.setShareMedia(umImage);
        }
        details.setTargetUrl(Constants.Link.GOOGLE_PLAY_URL + Constants.APP_NAME);
        controller.setShareMedia(details);
    }

    private static void setSINAShareDetails(Activity activity, Share share) {
        SinaShareContent details = new SinaShareContent();
        share.setContent("#澳妈圈#我刚刚在@澳妈圈 上分享了照片：" + share.getTitle());
        setShareDetails(activity, details, share);
    }

    private static void setWEIXINShareDetails(Activity activity, Share share) {
        WeiXinShareContent details = new WeiXinShareContent();
        setShareDetails(activity, details, share);
    }

    private static void setCircleShareDetails(Activity activity, Share share) {
        CircleShareContent details = new CircleShareContent();
        setShareDetails(activity, details, share);
    }

    private static void setQQShareDetails(Activity activity, Share share) {
        QQShareContent details = new QQShareContent();
        setShareDetails(activity, details, share);
    }

    private static void addSINAPlatform(Activity activity) {
        controller.getConfig().setSsoHandler(new SinaSsoHandler(activity));
    }

    private static void addWEXINPlatform(Activity activity) {
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

    private static void addQQPlatform(Activity activity) {
        String appId = "1104116792";
        String appKey = "laxdxbEPeIrELtht";
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, appId, appKey);
        qqSsoHandler.addToSocialSDK();
    }
}
