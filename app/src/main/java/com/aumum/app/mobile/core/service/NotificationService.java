package com.aumum.app.mobile.core.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.group.GroupRequestsActivity;
import com.aumum.app.mobile.ui.user.UserSingleActivity;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.PreferenceUtils;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.contact.ContactRequestsActivity;

/**
 * Created by Administrator on 18/11/2014.
 */
public class NotificationService {
    private Context context;

    public NotificationService(Context context) {
        this.context = context;
    }

    private NotificationCompat.Builder getNotificationBuilder(String title,
                                                              String content,
                                                              String largeIconUrl) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setLights(0xffff0000, 300, 300)
                .setSmallIcon(R.drawable.ic_launcher_notification)
                .setContentTitle(title)
                .setContentText(content);
        if (largeIconUrl != null) {
            Bitmap largeIcon = ImageLoaderUtils.loadImage(largeIconUrl);
            builder.setLargeIcon(largeIcon);
        }
        return builder;
    }

    private void notify(NotificationCompat.Builder builder, Intent intent) {
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

    public void pushContactInvitedNotification(String userName,
                                               String reason,
                                               String avatarUrl) {
        String content = context.getString(R.string.label_contact_invited, userName);
        NotificationCompat.Builder builder =
                getNotificationBuilder(content, reason, avatarUrl);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ContactRequestsActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }

    public void pushContactAgreedNotification(String userId,
                                              String userName,
                                              String avatarUrl) {
        String content = context.getString(R.string.label_contact_agreed);
        NotificationCompat.Builder builder =
                getNotificationBuilder(userName, content, avatarUrl);
        Intent intent = new Intent();
        intent.putExtra(ChatActivity.INTENT_ID, userId);
        intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
        intent.putExtra(ChatActivity.INTENT_TITLE, userName);
        intent.setComponent(new ComponentName(context, ChatActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }

    public void pushGroupAppliedNotification(String userName,
                                             String groupName,
                                             String reason,
                                             String avatarUrl) {
        String content = context.getString(R.string.label_group_applied, userName, groupName);
        NotificationCompat.Builder builder =
                getNotificationBuilder(content, reason, avatarUrl);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, GroupRequestsActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }

    public void pushGroupApprovedNotification(String groupId,
                                              String groupName) {
        String content = context.getString(R.string.label_group_approved);
        NotificationCompat.Builder builder =
                getNotificationBuilder(groupName, content, null);
        Intent intent = new Intent();
        intent.putExtra(ChatActivity.INTENT_ID, groupId);
        intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_GROUP);
        intent.putExtra(ChatActivity.INTENT_TITLE, groupName);
        intent.setComponent(new ComponentName(context, ChatActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }

    public void pushUserDetailsNotification(String userId,
                                            String title,
                                            String content,
                                            String avatarUrl) {
        NotificationCompat.Builder builder =
                getNotificationBuilder(title, content, avatarUrl);
        Intent intent = new Intent();
        intent.putExtra(UserSingleActivity.INTENT_USER_ID, userId);
        intent.setComponent(new ComponentName(context, UserSingleActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }
}
