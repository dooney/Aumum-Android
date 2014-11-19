package com.aumum.app.mobile.core.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.ui.party.PartyCommentsActivity;
import com.aumum.app.mobile.ui.party.PartyDetailsActivity;
import com.aumum.app.mobile.utils.Ln;
import com.parse.ParsePush;

/**
 * Created by Administrator on 18/11/2014.
 */
public class NotificationService {

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

    public void pushUserMessageNotification(Context context, String title, Message message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setVibrate(new long[]{0, 100, 200, 300})
                        .setLights(0xd9534f, 300, 1000)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setContentText(message.getContent());

        Intent intent = getUserMessageIntent(context, message);
        PendingIntent notifyIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notifyIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private Intent getUserMessageIntent(Context context, Message message) {
        Intent intent = new Intent();
        switch (message.getType()) {
            case Message.Type.PARTY_NEW:
            case Message.Type.PARTY_JOIN:
            case Message.Type.PARTY_QUIT:
            case Message.Type.PARTY_LIKE:
                if (message.getParentId() != null) {
                    intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, message.getParentId());
                    intent.setComponent(new ComponentName(context, PartyDetailsActivity.class));
                }
                break;
            case Message.Type.PARTY_COMMENT:
            case Message.Type.PARTY_REPLY:
                if (message.getParentId() != null) {
                    intent.putExtra(PartyCommentsActivity.INTENT_PARTY_ID, message.getParentId());
                    intent.setComponent(new ComponentName(context, PartyCommentsActivity.class));
                }
                break;
            case Message.Type.DELETED:
                break;
            case Message.Type.PARTY_CHECK_IN:
                break;
            default:
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
