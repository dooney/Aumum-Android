package com.aumum.app.mobile.ui.browser;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;
import com.aumum.app.mobile.ui.view.video.VideoEnabledWebChromeClient;
import com.aumum.app.mobile.ui.view.video.VideoEnabledWebView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 14/01/2015.
 */
public class BrowserActivity extends BaseActionBarActivity {

    @InjectView(R.id.webView) protected VideoEnabledWebView webView;
    @InjectView(R.id.pb_loading) protected ProgressBar progressBar;

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_URL = "url";
    public static final String INTENT_FULLSCREEN = "fullscreen";
    public static final String INTENT_LANDSCAPE = "landscape";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        if (intent.hasExtra(INTENT_TITLE)) {
            setTitle(intent.getStringExtra(INTENT_TITLE));
        }
        if (intent.getBooleanExtra(INTENT_FULLSCREEN, false)) {
            getSupportActionBar().hide();
        }
        if (intent.getBooleanExtra(INTENT_LANDSCAPE, false)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        View nonVideoLayout = findViewById(R.id.nonVideoLayout);
        ViewGroup videoLayout = (ViewGroup)findViewById(R.id.videoLayout);
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);
        VideoEnabledWebChromeClient webChromeClient =
                new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView);
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                    getSupportActionBar().hide();
                } else {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                    getSupportActionBar().show();
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView webView, String url, Bitmap favicon) {
                webView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });

        String url = intent.getStringExtra(INTENT_URL);
        webView.loadUrl(url);
    }
}
