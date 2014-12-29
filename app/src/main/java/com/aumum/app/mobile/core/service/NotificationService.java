package com.aumum.app.mobile.core.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.contact.ContactRequestsActivity;
import com.aumum.app.mobile.ui.party.PartyCommentsSingleActivity;
import com.aumum.app.mobile.ui.party.PartyDetailsSingleActivity;
import com.aumum.app.mobile.utils.Ln;
import com.parse.ParsePush;

/**
 * Created by Administrator on 18/11/2014.
 */
public class NotificationService {
    private Context context;

    public NotificationService(Context context) {
        this.context = context;
    }

    public void subscribe(String channel) {
        if (channel != null) {
            try {
                ParsePush.subscribeInBackground(channel);
            } catch (Exception e) {
                Ln.d(e);
            }
        }
    }

    public void unSubscribe(String channel) {
        if (channel != null) {
            try {
                ParsePush.unsubscribeInBackground(channel);
            } catch (Exception e) {
                Ln.d(e);
            }
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String title, String content) {
        return new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setVibrate(new long[]{0, 100, 200, 300})
                .setLights(0xd9534f, 300, 1000)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(content);
    }

    private void notify(NotificationCompat.Builder builder, Intent intent) {
        PendingIntent notifyIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notifyIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    public void pushPartyDetailsNotification(String partyId, String title, String content) {
        NotificationCompat.Builder builder = getNotificationBuilder(title, content);
        Intent intent = new Intent();
        intent.putExtra(PartyDetailsSingleActivity.INTENT_PARTY_ID, partyId);
        intent.setComponent(new ComponentName(context, PartyDetailsSingleActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }

    public void pushPartyCommentsNotification(String partyId, String title, String content) {
        NotificationCompat.Builder builder = getNotificationBuilder(title, content);
        Intent intent = new Intent();
        intent.putExtra(PartyCommentsSingleActivity.INTENT_PARTY_ID, partyId);
        intent.setComponent(new ComponentName(context, PartyCommentsSingleActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }

    public void pushContactInvitedNotification(String userName, String reason) {
        String content = context.getString(R.string.label_contact_invited, userName);
        NotificationCompat.Builder builder = getNotificationBuilder(content, reason);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ContactRequestsActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }

    public void pushContactAgreedNotification(String userId, String userName) {
        String content = context.getString(R.string.label_contact_agreed);
        NotificationCompat.Builder builder = getNotificationBuilder(userName, content);
        Intent intent = new Intent();
        intent.putExtra(ChatActivity.INTENT_ID, userId);
        intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
        intent.putExtra(ChatActivity.INTENT_TITLE, userName);
        intent.setComponent(new ComponentName(context, ChatActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notify(builder, intent);
    }
}
