package com.aumum.app.mobile.utils;

import android.app.Activity;
import android.content.Context;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.TuAlbumComponent;
import org.lasque.tusdk.impl.components.TuAvatarComponent;
import org.lasque.tusdk.impl.components.base.TuSdkComponent;
import org.lasque.tusdk.impl.components.camera.TuCameraOption;
import org.lasque.tusdk.impl.components.edit.TuEditTurnAndCutOption;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 30/04/2015.
 */
public class TuSdkUtils {

    public static interface ImageListener {
        void onImage(String imagePath);
    }

    public static interface FileListener {
        void onFile(File file);
    }

    public static void init(Context context) {
        String filePath = context.getExternalCacheDir().getPath() + "/lasFilterTemp/tusdk.statistics";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        TuSdk.init(context, "003d2448e9edf965-00-2j9nn1");
    }

    public static void album(Activity activity,
                             final ImageListener listener) {
        TuAlbumComponent comp = TuSdk.albumCommponent(activity,
                new TuSdkComponent.TuSdkComponentDelegate()
                {
                    @Override
                    public void onComponentFinished(TuSdkResult result,
                                                    Error error,
                                                    TuFragment lastFragment)
                    {
                        if (listener != null) {
                            listener.onImage(result.imageSqlInfo.path);
                        }
                    }
                });
        comp.setAutoDismissWhenCompleted(true).showComponent();
    }

    public static void avatar(Activity activity,
                              final FileListener listener) {
        TuAvatarComponent comp = TuSdk.avatarCommponent(activity,
                new TuSdkComponent.TuSdkComponentDelegate()
                {
                    @Override
                    public void onComponentFinished(TuSdkResult result,
                                                    Error error,
                                                    TuFragment lastFragment)
                    {
                        if (listener != null) {
                            listener.onFile(result.imageFile);
                        }
                    }
                });
        TuCameraOption cameraOption = comp.componentOption().cameraOption();
        cameraOption.setSaveToTemp(true);
        cameraOption.setSaveToAlbum(false);
        cameraOption.setFilterGroup(new ArrayList<String>());
        TuEditTurnAndCutOption turnAndCutOption = comp.componentOption().editTurnAndCutOption();
        turnAndCutOption.setSaveToTemp(true);
        turnAndCutOption.setSaveToAlbum(false);
        comp.setAutoDismissWhenCompleted(true).showComponent();
    }
}
