

package com.aumum.app.mobile;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import com.aumum.app.mobile.utils.EMChatUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.NotificationUtils;
import com.aumum.app.mobile.utils.SmsSdkUtils;
import com.aumum.app.mobile.utils.UpYunUtils;

/**
 * Aumum application
 */
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
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Perform injection
        Injector.init(getRootModule(), this);

        NotificationUtils.init(this);
        ImageLoaderUtils.init(this);
        EMChatUtils.init(this);
        UpYunUtils.init();
        SmsSdkUtils.init(this);
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
