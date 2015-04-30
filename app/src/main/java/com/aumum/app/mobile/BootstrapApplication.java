

package com.aumum.app.mobile;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.aumum.app.mobile.utils.EMChatUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.LocaleUtils;
import com.aumum.app.mobile.utils.PreferenceUtils;
import com.aumum.app.mobile.utils.PushUtils;
import com.aumum.app.mobile.utils.SmsSdkUtils;
import com.aumum.app.mobile.utils.TuSdkUtils;
import com.aumum.app.mobile.utils.UMengUtils;
import com.aumum.app.mobile.utils.UpYunUtils;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Aumum application
 */
@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=082a75bc")
public class BootstrapApplication extends Application {

    private static BootstrapApplication instance;

    /**
     * Create main application
     */
    public BootstrapApplication() {
    }

    /**
     * Create main application
     *
     * @param context
     */
    public BootstrapApplication(final Context context) {
        this();
        attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Perform injection
        Injector.init(getRootModule(), this);

        ACRA.init(this);
        UMengUtils.init(this);
        PushUtils.init(this);
        LocaleUtils.init(this);
        PreferenceUtils.init(this);
        EMChatUtils.init(this);
        UpYunUtils.init();
        SmsSdkUtils.init(this);
        TuSdkUtils.init(this);
        ImageLoaderUtils.init(this);
    }

    private Object getRootModule() {
        return new RootModule();
    }

    /**
     * Create main application
     *
     * @param instrumentation
     */
    public BootstrapApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    public static BootstrapApplication getInstance() {
        return instance;
    }
}
