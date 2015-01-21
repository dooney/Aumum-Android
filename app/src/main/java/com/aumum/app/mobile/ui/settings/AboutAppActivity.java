package com.aumum.app.mobile.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.ui.browser.BrowserActivity;
import com.aumum.app.mobile.ui.view.Animation;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 13/01/2015.
 */
public class AboutAppActivity extends ActionBarActivity {

    @InjectView(R.id.layout_rate_app) protected View rateAppLayout;
    @InjectView(R.id.text_app_version) protected TextView versionText;
    @InjectView(R.id.layout_agreement) protected View agreementLayout;
    @InjectView(R.id.layout_check_for_updates) protected View checkForUpdatesLayout;
    @InjectView(R.id.text_weibo_info) protected TextView weiboInfoText;
    @InjectView(R.id.text_website_info) protected TextView websiteInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.inject(this);

        showAppVersion();
        rateAppLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGooglePlayActivity();
            }
        });
        agreementLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAgreement();
            }
        });
        checkForUpdatesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGooglePlayActivity();
            }
        });
        websiteInfoText.setText(Html.fromHtml(getString(R.string.label_website_info)));
        websiteInfoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWebsite();
            }
        });
        weiboInfoText.setText(Html.fromHtml(getString(R.string.label_weibo_info)));
        weiboInfoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Animation.flyIn(this);
    }

    private void showAppVersion() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionText.setText(versionName);
        } catch (Exception e) {
            versionText.setText(R.string.label_unknown_app_version);
        }
    }

    private void startGooglePlayActivity() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Link.GOOGLE_PLAY_APP + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Link.GOOGLE_PLAY_URL + appPackageName)));
        }
    }

    private void showAgreement() {
        final Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.INTENT_TITLE, getString(R.string.label_agreement));
        intent.putExtra(BrowserActivity.INTENT_URL, Constants.Link.AGREEMENT);
        startActivity(intent);
    }

    private void showWebsite() {
        final Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.INTENT_URL, Constants.Link.HOME);
        startActivity(intent);
    }
}
