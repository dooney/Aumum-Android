package com.keyboard.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.TextView;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.db.DBHelper;
import com.keyboard.utils.imageloader.ImageLoader;
import com.keyboard.view.VerticalImageSpan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils
 * @author zhongdaxia 2014-9-2 12:05:55
 */

public class EmoticonsRegexUtils {

    public static ArrayList<EmoticonBean> emoticonBeanList = null;

    private static int getFontHeight(TextView tv) {
        Paint paint = new Paint();
        paint.setTextSize(tv.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }

    public static boolean setTextFace(final Context context, TextView tv, Object spannable) {
        boolean isEmoticonMatcher = false;
        Pattern p = Pattern.compile (
                "\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]|[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]" ,
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE) ;
        String text = spannable.toString();
        Matcher m = p.matcher(text);
        if(m != null){
            while (m.find()) {
                if (emoticonBeanList == null) {
                    DBHelper dbHelper = new DBHelper(context);
                    emoticonBeanList = dbHelper.queryAllEmoticonBeans();
                    dbHelper.cleanup();
                    if (emoticonBeanList == null) {
                        return isEmoticonMatcher;
                    }
                }

                int start = m.start();
                int end = m.end();
                String key = text.substring(start, end);
                int fontHeight = getFontHeight(tv);
                for (EmoticonBean bean : emoticonBeanList) {
                    if (!TextUtils.isEmpty(bean.getContent()) && bean.getContent().equals(key)) {
                        Drawable drawable = ImageLoader.getInstance(context).getDrawable(bean.getIconUri());
                        if (drawable != null) {
                            drawable.setBounds(0, 0, fontHeight, fontHeight);
                            VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
                            if (spannable instanceof SpannableString) {
                                ((SpannableString) spannable).setSpan(imageSpan, start ,end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            if (spannable instanceof SpannableStringBuilder) {
                                ((SpannableStringBuilder) spannable).setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            isEmoticonMatcher = true;
                        }
                    }
                }
            }
        }
        return isEmoticonMatcher;
    }

}
