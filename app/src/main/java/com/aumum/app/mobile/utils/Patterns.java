package com.aumum.app.mobile.utils;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 5/12/2014.
 */
public class Patterns {
    public static final Pattern WEB_URL = Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]");

    public static final Pattern TOPIC_URL = Pattern.compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#");

    public static final Pattern MENTION_URL = Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}");

    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");


    public static final String WEB_SCHEME = "http://";

    public static final String TOPIC_SCHEME = "com.aumum.app.mobile.topic://";

    public static final String MENTION_SCHEME = "com.aumum.app.mobile.user://";
}
