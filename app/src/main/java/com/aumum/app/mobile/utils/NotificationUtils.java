package com.aumum.app.mobile.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 18/11/2014.
 */
public class NotificationUtils {

    public static void notify(Context context,
                              String title,
                              String content,
                              String largeIconUrl,
                              Class<?> cls) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setLights(0xffff0000, 300, 300)
                .setContentTitle(title)
                .setContentText(content);
        if (largeIconUrl != null) {
            Bitmap largeIcon = ImageLoaderUtils.loadImage(largeIconUrl);
            builder.setLargeIcon(largeIcon);
        } else {
            builder.setSmallIcon(R.drawable.ic_launcher_notification);
        }

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, cls));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent notifyIntent = PendingIntent
                .getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notifyIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (PreferenceUtils.isNotificationSoundEnabled()) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        if (PreferenceUtils.isNotificationVibrateEnabled()) {
            builder.setVibrate(new long[]{0, 200, 200, 200});
        }
        mNotificationManager.notify(0, builder.build());
    }
}
