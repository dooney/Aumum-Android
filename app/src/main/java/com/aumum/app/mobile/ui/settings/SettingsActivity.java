package com.aumum.app.mobile.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Share;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.LogoutService;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;
import com.aumum.app.mobile.ui.browser.BrowserActivity;
import com.aumum.app.mobile.ui.view.ToggleButton;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.TextViewDialog;
import com.aumum.app.mobile.utils.PreferenceUtils;
import com.aumum.app.mobile.utils.ShareUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsActivity extends BaseActionBarActivity {

    @Inject UserStore userStore;
    @Inject LogoutService logoutService;
    @Inject ChatService chatService;

    @InjectView(R.id.b_switch_sound) protected ToggleButton soundSwitch;
    @InjectView(R.id.b_switch_vibrate) protected ToggleButton vibrateSwitch;
    @InjectView(R.id.layout_about_app) protected View aboutAppLayout;
    @InjectView(R.id.layout_feedback) protected View feedbackLayout;
    @InjectView(R.id.layout_share_app) protected View shareAppLayout;
    @InjectView(R.id.layout_agreement) protected View agreementLayout;
    @InjectView(R.id.b_logout) protected View logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        initNotificationSettings();

        aboutAppLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAboutAppActivity();
            }
        });
        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFeedbackActivity();
            }
        });
        shareAppLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    User currentUser = userStore.getCurrentUser();
                    String content = getString(R.string.label_share_app_text,
                            currentUser.getScreenName());
                    Share share = new Share(content, content, null);
                    ShareUtils.show(SettingsActivity.this, share);
                } catch (Exception e) {
                    showError(e);
                }
            }
        });
        agreementLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAgreement();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmDialog();
            }
        });
    }

    private void initNotificationSettings() {
        if (PreferenceUtils.isNotificationSoundEnabled()) {
            soundSwitch.setToggleOn();
        } else {
            soundSwitch.setToggleOff();
        }
        soundSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                chatService.setNotificationSoundEnabled(on);
                PreferenceUtils.setNotificationSoundEnabled(on);
            }
        });
        if (PreferenceUtils.isNotificationVibrateEnabled()) {
            vibrateSwitch.setToggleOn();
        } else {
            vibrateSwitch.setToggleOff();
        }
        vibrateSwitch.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                chatService.setNotificationVibrateEnabled(on);
                PreferenceUtils.setNotificationVibrateEnabled(on);
            }
        });
    }

    private void showLogoutConfirmDialog() {
        new TextViewDialog(this, getString(R.string.info_confirm_logout),
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        logoutService.logout();
                        chatService.logOut();
                    }

                    @Override
                    public void onException(String errorMessage) {
                        showMsg(errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        final Intent intent = new Intent();
                        intent.putExtra("logout", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).show();
    }

    private void startAboutAppActivity() {
        final Intent intent = new Intent(this, AboutAppActivity.class);
        startActivity(intent);
    }

    private void startFeedbackActivity() {
        final Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    private void showAgreement() {
        final Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.INTENT_TITLE, getString(R.string.label_agreement));
        intent.putExtra(BrowserActivity.INTENT_URL, Constants.Link.AGREEMENT);
        startActivity(intent);
    }
}
