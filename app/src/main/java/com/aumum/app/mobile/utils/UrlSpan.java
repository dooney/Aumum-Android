package com.aumum.app.mobile.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Browser;
import android.text.*;
import android.text.style.ClickableSpan;
import android.view.View;

import com.aumum.app.mobile.ui.browser.BrowserActivity;

/**
 * Created by Administrator on 5/12/2014.
 */
public class UrlSpan extends ClickableSpan implements View.OnClickListener {
    private final String mURL;

    public UrlSpan(String url) {
        mURL = url;
    }

    public String getURL() {
        return mURL;
    }

    @Override
    public void onClick(View widget) {
        Uri uri = Uri.parse(getURL());
        Context context = widget.getContext();
        if (uri.getScheme().startsWith("http")) {
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra(BrowserActivity.INTENT_URL, uri.toString());
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        }
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(Color.parseColor("#005684"));
    }
}