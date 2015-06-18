package com.aumum.app.mobile.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
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
                              Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setLights(0xffff0000, 300, 300)
                .setTicker(content)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(content);
        if (largeIconUrl == null) {
            largeIconUrl = "drawable://" + R.drawable.ic_launcher;
        }
        Bitmap largeIcon = ImageLoaderUtils.loadImage(largeIconUrl);
        builder.setLargeIcon(largeIcon);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyIntent = PendingIntent
                .getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notifyIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (PreferenceUtils.isNotificationSoundEnabled()) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        if (PreferenceUtils.isNotificationVibrateEnabled()) {
            builder.setVibrate(new long[]{0, 200, 200, 200});
        }
        notificationManager.notify(0, builder.build());
    }
}
