package com.aumum.app.mobile.utils;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 15/03/2015.
 */
public class FeedItemUtils {

    private static final Whitelist JSOUP_WHITELIST = Whitelist.relaxed().addTags("iframe", "video", "audio", "source", "track")
            .addAttributes("iframe", "src", "frameborder", "height", "width")
            .addAttributes("video", "src", "controls", "height", "width", "poster")
            .addAttributes("audio", "src", "controls")
            .addAttributes("source", "src", "type")
            .addAttributes("track", "src", "kind", "srclang", "label");

    // middle() is group 1; s* is important for non-whitespaces; ' also usable
    private static final Pattern IMG_PATTERN = Pattern.compile("<img\\s+[^>]*src=\\s*['\"]([^'\"]+)['\"][^>]*>", Pattern.CASE_INSENSITIVE);
    private static final String URL_SPACE = "%20";

    public static String improveHtmlContent(String content, String baseUri) {
        if (content != null) {
            // remove some ads
            content = content.replaceAll("(?i)<div class=('|\")mf-viral('|\")><table border=('|\")0('|\")>.*", "");
            // remove lazy loading images stuff
            content = content.replaceAll("(?i)\\s+src=[^>]+\\s+original[-]*src=(\"|')", " src=$1");
            // remove bad image paths
            content = content.replaceAll("(?i)\\s+(href|src)=(\"|')//", " $1=$2http://");
            // clean by jsoup
            content = Jsoup.clean(content, baseUri, JSOUP_WHITELIST);
        }

        return content;
    }

    public static ArrayList<String> getImageURLs(String content) {
        ArrayList<String> images = new ArrayList<>();
        if (!TextUtils.isEmpty(content)) {
            Matcher matcher = IMG_PATTERN.matcher(content);
            while (matcher.find()) {
                images.add(matcher.group(1).replace(" ", URL_SPACE));
            }
        }
        return images;
    }

    public static String getTextFromHtml(String html) {
        return Jsoup.parse(html).text();
    }
}
