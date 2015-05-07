package com.aumum.app.mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.hardware.CameraHelper;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.TuAlbumComponent;
import org.lasque.tusdk.impl.components.TuEditComponent;
import org.lasque.tusdk.impl.components.base.TuSdkComponent;
import org.lasque.tusdk.impl.components.base.TuSdkHelperComponent;
import org.lasque.tusdk.impl.components.camera.TuCameraFragment;
import org.lasque.tusdk.impl.components.camera.TuCameraOption;
import org.lasque.tusdk.impl.components.edit.TuEditEntryOption;
import org.lasque.tusdk.impl.components.edit.TuEditTurnAndCutFragment;
import org.lasque.tusdk.impl.components.edit.TuEditTurnAndCutOption;

import java.io.File;

/**
 * Created by Administrator on 30/04/2015.
 */
public class TuSdkUtils {

    public static interface CameraListener {
        void onCameraResult(ImageSqlInfo imageSqlInfo);
    }

    public static interface AlbumListener {
        void onAlbumResult(ImageSqlInfo imageSqlInfo);
    }

    public static interface CropListener {
        void onCropResult(File file);
    }

    public static interface EditListener {
        void onEditResult(File file);
    }

    public static void init(Context context) {
        String filePath = context.getExternalCacheDir().getPath() + "/lasFilterTemp/tusdk.statistics";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        TuSdk.init(context, "003d2448e9edf965-00-2j9nn1");
    }

    public static void camera(Activity activity,
                              final CameraListener listener) {
        if (CameraHelper.showAlertIfNotSupportCamera(activity)) {
            return;
        }
        TuSdkHelperComponent component = new TuSdkHelperComponent(activity);
        TuCameraOption option = new TuCameraOption();
        option.setSaveToAlbum(true);
        TuCameraFragment fragment = option.fragment();
        fragment.setDelegate(new TuCameraFragment.TuCameraFragmentDelegate() {
            @Override
            public void onTuCameraFragmentCaptured(TuCameraFragment tuCameraFragment,
                                                   TuSdkResult tuSdkResult) {
                tuCameraFragment.hubDismissRightNow();
                tuCameraFragment.dismissActivityWithAnim();
                if (listener != null) {
                    listener.onCameraResult(tuSdkResult.imageSqlInfo);
                }
            }

            @Override
            public boolean onTuCameraFragmentCapturedAsync(TuCameraFragment tuCameraFragment,
                                                           TuSdkResult tuSdkResult) {
                return false;
            }

            @Override
            public void onComponentError(TuFragment tuFragment,
                                         TuSdkResult tuSdkResult,
                                         Error error) {

            }
        });
        component.presentModalNavigationActivity(fragment, true);
    }

    public static void album(Activity activity,
                             final AlbumListener listener) {
        TuAlbumComponent component = TuSdk.albumCommponent(activity,
                new TuSdkComponent.TuSdkComponentDelegate() {
                    @Override
                    public void onComponentFinished(TuSdkResult result,
                                                    Error error,
                                                    TuFragment lastFragment) {
                        if (listener != null) {
                            listener.onAlbumResult(result.imageSqlInfo);
                        }
                    }
                });
        component.setAutoDismissWhenCompleted(true)
                .showComponent();
    }

    public static void edit(Activity activity,
                            Bitmap bitmap,
                            boolean isEnableCuter,
                            boolean isEnableFilter,
                            boolean isEnableSticker,
                            final EditListener listener) {
        TuEditComponent component = TuSdk.editCommponent(activity,
                new TuSdkComponent.TuSdkComponentDelegate() {
            @Override
            public void onComponentFinished(TuSdkResult tuSdkResult,
                                            Error error,
                                            TuFragment tuFragment) {
                if (listener != null) {
                    listener.onEditResult(tuSdkResult.imageFile);
                }
            }
        });
        TuEditEntryOption option = component.componentOption().editEntryOption();
        option.setSaveToTemp(true);
        option.setEnableCuter(isEnableCuter);
        option.setEnableFilter(isEnableFilter);
        option.setEnableSticker(isEnableSticker);
        component.setImage(bitmap)
                .setAutoDismissWhenCompleted(true)
                .showComponent();
    }

    public static void crop(Activity activity,
                            ImageSqlInfo imageSqlInfo,
                            final CropListener listener) {
        TuSdkHelperComponent component = new TuSdkHelperComponent(activity);
        TuEditTurnAndCutOption option = new TuEditTurnAndCutOption();
        option.setCutSize(new TuSdkSize(640, 640));
        option.setSaveToTemp(true);
        TuEditTurnAndCutFragment fragment = option.fragment();
        fragment.setImageSqlInfo(imageSqlInfo);
        fragment.setDelegate(new TuEditTurnAndCutFragment.TuEditTurnAndCutFragmentDelegate() {
            @Override
            public void onTuEditTurnAndCutFragmentEdited(TuEditTurnAndCutFragment tuEditTurnAndCutFragment,
                                                         TuSdkResult tuSdkResult) {
                tuEditTurnAndCutFragment.hubDismissRightNow();
                tuEditTurnAndCutFragment.dismissActivityWithAnim();
                if (listener != null) {
                    listener.onCropResult(tuSdkResult.imageFile);
                }
            }

            @Override
            public boolean onTuEditTurnAndCutFragmentEditedAsync(TuEditTurnAndCutFragment tuEditTurnAndCutFragment,
                                                                 TuSdkResult tuSdkResult) {
                return false;
            }

            @Override
            public void onComponentError(TuFragment tuFragment,
                                         TuSdkResult tuSdkResult,
                                         Error error) {

            }
        });
        component.presentModalNavigationActivity(fragment, true);
    }
}
