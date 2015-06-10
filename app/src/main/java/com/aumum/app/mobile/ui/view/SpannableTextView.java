package com.aumum.app.mobile.ui.view;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.aumum.app.mobile.utils.Patterns;
import com.aumum.app.mobile.utils.UrlSpan;
import com.keyboard.utils.EmoticonsRegexUtils;

/**
 * Created by Administrator on 6/12/2014.
 */
public class SpannableTextView extends TextView {
    public SpannableTextView(Context context) {
        super(context);
    }

    public SpannableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpannableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSpannableText(String text) {
        super.setText(getSpannableString(text));
    }

    private SpannableString getSpannableString(String txt) {
        SpannableString value = SpannableString.valueOf(txt);
        Linkify.addLinks(value, Patterns.MENTION_URL, Patterns.MENTION_SCHEME);
        Linkify.addLinks(value, Patterns.WEB_URL, Patterns.WEB_SCHEME);

        URLSpan[] urlSpans = value.getSpans(0, value.length(), URLSpan.class);
        UrlSpan span;
        for (URLSpan urlSpan : urlSpans) {
            span = new UrlSpan(urlSpan.getURL());
            int start = value.getSpanStart(urlSpan);
            int end = value.getSpanEnd(urlSpan);
            value.removeSpan(urlSpan);
            value.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        EmoticonsRegexUtils.setTextFace(getContext(), this, value);
        return value;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = false;
        CharSequence text = getText();
        Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= getTotalPaddingLeft();
            y -= getTotalPaddingTop();

            x += getScrollX();
            y += getScrollY();

            Layout layout = getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(this);
                }
                ret = true;
            } else {
                performClick();
            }
        }
        return ret;
    }
}
