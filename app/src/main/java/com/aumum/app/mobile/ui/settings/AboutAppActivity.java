package com.aumum.app.mobile.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.inject(this);

        showAppVersion();
        rateAppLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRateAppActivity();
            }
        });
        agreementLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAgreement();
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

    private void startRateAppActivity() {
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
}
