package com.aumum.app.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 16/01/2015.
 */
public class PreferenceUtils {

    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;

    private static final String PREFERENCE_NAME = "aumum.com";
    private static final String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
    private static final String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static boolean isNotificationSoundEnabled() {
        return sharedPreferences.getBoolean(SHARED_KEY_SETTING_SOUND, true);
    }

    public static boolean isNotificationVibrateEnabled() {
        return sharedPreferences.getBoolean(SHARED_KEY_SETTING_VIBRATE, true);
    }

    public static void setNotificationSoundEnabled(boolean isEnabled) {
        editor.putBoolean(SHARED_KEY_SETTING_SOUND, isEnabled);
        editor.commit();
    }

    public static void setNotificationVibrateEnabled(boolean isEnabled) {
        editor.putBoolean(SHARED_KEY_SETTING_VIBRATE, isEnabled);
        editor.commit();
    }
}
