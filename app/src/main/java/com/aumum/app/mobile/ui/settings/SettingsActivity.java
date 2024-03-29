package com.aumum.app.mobile.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.LogoutService;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsActivity extends ActionBarActivity {

    @Inject LogoutService logoutService;
    @Inject ChatService chatService;

    @InjectView(R.id.layout_notification) protected View notificationLayout;
    @InjectView(R.id.layout_feedback) protected View feedbackLayout;
    @InjectView(R.id.layout_about_app) protected View aboutAppLayout;
    @InjectView(R.id.layout_logout) protected View logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNotificationActivity();
            }
        });
        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFeedbackActivity();
            }
        });
        aboutAppLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAboutAppActivity();
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmDialog();
            }
        });

        Animation.flyIn(this);
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
                        Toaster.showShort(SettingsActivity.this, errorMessage);
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

    private void startNotificationActivity() {
        final Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    private void startFeedbackActivity() {
        final Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    private void startAboutAppActivity() {
        final Intent intent = new Intent(this, AboutAppActivity.class);
        startActivity(intent);
    }
}
