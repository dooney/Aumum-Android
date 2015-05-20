package com.aumum.app.mobile.ui.settings;

import android.os.Bundle;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;
import com.aumum.app.mobile.ui.view.ToggleButton;
import com.aumum.app.mobile.utils.PreferenceUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 16/01/2015.
 */
public class NotificationActivity extends BaseActionBarActivity {

    @Inject ChatService chatService;

    @InjectView(R.id.image_switch_sound) protected ToggleButton soundToggleButton;
    @InjectView(R.id.image_switch_vibrate) protected ToggleButton vibrateToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.inject(this);

        if (PreferenceUtils.isNotificationSoundEnabled()) {
            soundToggleButton.setToggleOn();
        } else {
            soundToggleButton.setToggleOff();
        }
        soundToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                chatService.setNotificationSoundEnabled(on);
                PreferenceUtils.setNotificationSoundEnabled(on);
            }
        });
        if (PreferenceUtils.isNotificationVibrateEnabled()) {
            vibrateToggleButton.setToggleOn();
        } else {
            vibrateToggleButton.setToggleOff();
        }
        vibrateToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                chatService.setNotificationVibrateEnabled(on);
                PreferenceUtils.setNotificationVibrateEnabled(on);
            }
        });
    }
}
