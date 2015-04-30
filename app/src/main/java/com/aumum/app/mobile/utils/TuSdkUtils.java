package com.aumum.app.mobile.utils;

import android.app.Activity;
import android.content.Context;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.TuAlbumComponent;
import org.lasque.tusdk.impl.components.base.TuSdkComponent;

/**
 * Created by Administrator on 30/04/2015.
 */
public class TuSdkUtils {

    public static void init(Context context) {
        TuSdk.init(context, "003d2448e9edf965-00-2j9nn1");
    }

    public static void openAlbum(Activity activity) {
        TuAlbumComponent comp = TuSdk.albumCommponent(activity,
                new TuSdkComponent.TuSdkComponentDelegate()
                {
                    @Override
                    public void onComponentFinished(TuSdkResult result,
                                                    Error error,
                                                    TuFragment lastFragment)
                    {

                    }
                });
        comp.setAutoDismissWhenCompleted(true).showComponent();
    }
}
