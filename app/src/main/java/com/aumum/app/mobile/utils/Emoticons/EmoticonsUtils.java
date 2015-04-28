package com.aumum.app.mobile.utils.Emoticons;

import android.content.Context;
import android.text.TextUtils;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.keyboard.utils.DefEmoticons;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.utils.Utils;
import com.keyboard.utils.imageloader.ImageBase;

import java.util.ArrayList;

public class EmoticonsUtils {

    /**
     * 初始化表情数据库
     * @param context
     */
    public static void initEmoticonsDB(final Context context) {
        if (!Utils.isInitDb(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DBHelper dbHelper = new DBHelper(context);

                    /**
                     * FROM ASSETS
                     */
                    ArrayList<EmoticonBean> emojiArray = ParseData(DefEmoticons.emojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.ASSETS);
                    EmoticonSetBean emojiEmoticonSetBean = new EmoticonSetBean("emoji", 3, 7);
                    emojiEmoticonSetBean.setIconUri("assets://icon_emoji");
                    emojiEmoticonSetBean.setItemPadding(20);
                    emojiEmoticonSetBean.setVerticalSpacing(10);
                    emojiEmoticonSetBean.setShowDelBtn(true);
                    emojiEmoticonSetBean.setEmoticonList(emojiArray);
                    dbHelper.insertEmoticonSet(emojiEmoticonSetBean);

                    ArrayList<EmoticonBean> xhsFaceArray = ParseData(xhsEmojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.ASSETS);
                    EmoticonSetBean xhsEmoticonSetBean = new EmoticonSetBean("xhs", 3, 7);
                    xhsEmoticonSetBean.setIconUri("assets://xhsemoji_19.png");
                    xhsEmoticonSetBean.setItemPadding(20);
                    xhsEmoticonSetBean.setVerticalSpacing(10);
                    xhsEmoticonSetBean.setShowDelBtn(true);
                    xhsEmoticonSetBean.setEmoticonList(xhsFaceArray);
                    dbHelper.insertEmoticonSet(xhsEmoticonSetBean);

                    ArrayList<EmoticonBean> coverFaceArray = ParseData(coverEmojiArray, EmoticonBean.FACE_TYPE_NOMAL, ImageBase.Scheme.ASSETS);
                    EmoticonSetBean coverEmoticonSetBean = new EmoticonSetBean("cover", 3, 7);
                    coverEmoticonSetBean.setIconUri("assets://icon_030_cover.png");
                    coverEmoticonSetBean.setItemPadding(20);
                    coverEmoticonSetBean.setVerticalSpacing(10);
                    coverEmoticonSetBean.setShowDelBtn(true);
                    coverEmoticonSetBean.setEmoticonList(coverFaceArray);
                    dbHelper.insertEmoticonSet(coverEmoticonSetBean);

                    /**
                     * FROM FILE
                     */


                    /**
                     * FROM HTTP/HTTPS
                     */


                    /**
                     * FROM CONTENT
                     */


                    /**
                     * FROM USER_DEFINED
                     */

                    dbHelper.cleanup();
                    Utils.setIsInitDb(context, true);
                }
            }).start();
        }
    }

    public static EmoticonsKeyboardBuilder getBuilder(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList = dbHelper.queryAllEmoticonSet();
        dbHelper.cleanup();

        ArrayList<AppBean> mAppBeanList = new ArrayList<>();
        String[] funcArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func);
        String[] funcIconArray = context.getResources().getStringArray(com.keyboard.view.R.array.apps_func_icon);
        for (int i = 0; i < funcArray.length; i++) {
            AppBean bean = new AppBean();
            bean.setId(i);
            bean.setIcon(funcIconArray[i]);
            bean.setFuncName(funcArray[i]);
            mAppBeanList.add(bean);
        }

        return new EmoticonsKeyboardBuilder.Builder()
                .setEmoticonSetBeanList(mEmoticonSetBeanList)
                .build();
    }

    public static ArrayList<EmoticonBean> ParseData(String[] arry, long eventType, ImageBase.Scheme scheme) {
        try {
            ArrayList<EmoticonBean> emojis = new ArrayList<EmoticonBean>();
            for (int i = 0; i < arry.length; i++) {
                if (!TextUtils.isEmpty(arry[i])) {
                    String temp = arry[i].trim().toString();
                    String[] text = temp.split(",");
                    if (text != null && text.length == 2) {
                        String fileName = null;
                        if (scheme == ImageBase.Scheme.DRAWABLE) {
                            if(text[0].contains(".")){
                                fileName = scheme.toUri(text[0].substring(0, text[0].lastIndexOf(".")));
                            }
                            else {
                                fileName = scheme.toUri(text[0]);
                            }
                        } else {
                            fileName = scheme.toUri(text[0]);
                        }
                        String content = text[1];
                        EmoticonBean bean = new EmoticonBean(eventType, fileName, content);
                        emojis.add(bean);
                    }
                }
            }
            return emojis;
        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }

        return null;
    }

    /*
    小红书表情
     */
    public static String[] xhsEmojiArray = {
            "xhsemoji_1.png,[无语]",
            "xhsemoji_2.png,[汗]",
            "xhsemoji_3.png,[瞎]",
            "xhsemoji_4.png,[口水]",
            "xhsemoji_5.png,[酷]",
            "xhsemoji_6.png,[哭] ",
            "xhsemoji_7.png,[萌]",
            "xhsemoji_8.png,[挖鼻孔]",
            "xhsemoji_9.png,[好冷]",
            "xhsemoji_10.png,[白眼]",
            "xhsemoji_11.png,[晕]",
            "xhsemoji_12.png,[么么哒]",
            "xhsemoji_13.png,[哈哈]",
            "xhsemoji_14.png,[好雷]",
            "xhsemoji_15.png,[啊]",
            "xhsemoji_16.png,[嘘]",
            "xhsemoji_17.png,[震惊]",
            "xhsemoji_18.png,[刺瞎]",
            "xhsemoji_19.png,[害羞]",
            "xhsemoji_20.png,[嘿嘿]",
            "xhsemoji_21.png,[嘻嘻]"
    };

    public static String[] coverEmojiArray = {
            "icon_002_cover.png, ",
            "icon_007_cover.png, ",
            "icon_010_cover.png, ",
            "icon_012_cover.png, ",
            "icon_013_cover.png, ",
            "icon_018_cover.png, ",
            "icon_019_cover.png, ",
            "icon_020_cover.png, ",
            "icon_021_cover.png, ",
            "icon_022_cover.png, ",
            "icon_024_cover.png, ",
            "icon_027_cover.png, ",
            "icon_029_cover.png, ",
            "icon_030_cover.png, ",
            "icon_035_cover.png, ",
            "icon_040_cover.png, "
    };
}
