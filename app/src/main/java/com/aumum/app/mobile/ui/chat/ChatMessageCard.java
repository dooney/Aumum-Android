package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;
import com.squareup.otto.Bus;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by Administrator on 11/11/2014.
 */
public abstract class ChatMessageCard
        implements SendMessageListener.OnActionListener,
                   ChatMessageListener {

    @Inject UserStore userStore;
    @Inject ApiKeyProvider apiKeyProvider;
    protected Bus bus;
    protected String chatId;
    protected Activity activity;

    private TextView timeStampText;
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private ProgressBar progressBar;
    private ImageView resendImage;
    private SendMessageListener listener;

    public ChatMessageCard(Activity activity,
                           Bus bus,
                           View view) {
        Injector.inject(this);
        this.activity = activity;
        this.bus = bus;
        timeStampText = (TextView) view.findViewById(R.id.text_time_stamp);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        resendImage = (ImageView) view.findViewById(R.id.image_sent_failed);
        listener = new SendMessageListener();
        listener.setListener(this);
        chatId = apiKeyProvider.getAuthUserId().toLowerCase();
    }

    @Override
    public void refresh(final EMMessage message, boolean showTimestamp, int position) {
        if (position == 0) {
            timeStampText.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            timeStampText.setVisibility(View.VISIBLE);
        } else {
            if (showTimestamp) {
                timeStampText.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timeStampText.setVisibility(View.VISIBLE);
            } else {
                timeStampText.setVisibility(View.GONE);
            }
        }

        String userName = activity.getString(R.string.label_unknown_user);
        String avatarUrl = null;
        try {
            User user = userStore.getUserByChatId(message.getFrom());
            userName = user.getScreenName();
            avatarUrl = user.getAvatarUrl();
            UserListener userListener = new UserListener(activity, user.getObjectId());
            if (userNameText != null) {
                userNameText.setOnClickListener(userListener);
            }
            avatarImage.setOnClickListener(userListener);
        } catch (Exception e) {
            Ln.e(e);
        }
        if (userNameText != null) {
            userNameText.setText(userName);
        }
        if (avatarUrl != null) {
            avatarImage.getFromUrl(avatarUrl);
        } else {
            avatarImage.setImageResource(R.drawable.ic_avatar);
        }

        if (resendImage != null) {
            resendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage(message);
                }
            });
        }
        if (message.direct == EMMessage.Direct.SEND ) {
            switch (message.status) {
                case INPROGRESS:
                    onProgress(message.progress, null);
                    break;
                case SUCCESS:
                    onSuccess();
                    break;
                case FAIL:
                    onError(0, null);
                    break;
                default:
                    sendMessage(message);
            }
        }
    }

    @Override
    public void onSuccess() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress().hideResend();
            }
        });
    }

    @Override
    public void onError(int code, String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show(resendImage).hideProgress();
            }
        });
    }

    @Override
    public void onProgress(int progress, String status) {
    }

    protected void sendMessage(final EMMessage message) {
        show(progressBar).hideResend();
        listener.sendMessage(message);
    }

    private ChatMessageCard show(final View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    private ChatMessageCard hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        return this;
    }

    private ChatMessageCard hideResend() {
        if (resendImage != null) {
            resendImage.setVisibility(View.GONE);
        }
        return this;
    }
}
