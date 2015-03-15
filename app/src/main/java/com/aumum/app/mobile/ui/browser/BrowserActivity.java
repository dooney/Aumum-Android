package com.aumum.app.mobile.ui.browser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.aumum.app.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 14/01/2015.
 */
public class BrowserActivity extends ActionBarActivity {

    @InjectView(R.id.webView) protected WebView webView;
    @InjectView(R.id.pb_loading) protected ProgressBar progressBar;

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        if (intent.hasExtra(INTENT_TITLE)) {
            setTitle(intent.getStringExtra(INTENT_TITLE));
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient());
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
