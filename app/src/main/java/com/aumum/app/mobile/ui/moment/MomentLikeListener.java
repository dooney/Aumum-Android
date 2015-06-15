package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.user.UserListActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.DisplayUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 10/05/2015.
 */
public class MomentLikeListener implements LikeTextView.LikeListener {

    private Activity activity;
    private Moment moment;
    private SafeAsyncTask<Boolean> task;

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject ChatService chatService;

    public MomentLikeListener(Activity activity,
                              Moment moment) {
        this.activity = activity;
        this.moment = moment;
        Injector.inject(this);
    }

    @Override
    public void onUnLike(LikeTextView view) {
        if (task != null) {
            return;
        }
        final View rootView = view.getRootView();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                restService.removeMomentLike(moment.getObjectId(),
                        currentUser.getObjectId());
                moment.removeLike(currentUser.getObjectId());
                moment.removeLikeInfo(currentUser.getObjectId());
                moment.setLiked(currentUser.getObjectId());
                momentStore.save(moment);
                return true;
            }

            @Override
            public void onSuccess(final Boolean success) {
                ViewGroup likesLayout = (ViewGroup) rootView.findViewById(R.id.layout_likes);
                updateLikesLayout(likesLayout, moment.getLikesInfo());
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onLike(LikeTextView view) {
        if (task != null) {
            return;
        }
        final View rootView = view.getRootView();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                restService.addMomentLike(moment.getObjectId(),
                        currentUser.getObjectId());
                moment.addLike(currentUser.getObjectId());
                moment.addLikeInfo(new UserInfo(currentUser.getObjectId(),
                        currentUser.getScreenName(), currentUser.getAvatarUrl()));
                moment.setLiked(currentUser.getObjectId());
                momentStore.save(moment);
                notifyMomentOwner(currentUser);
                return true;
            }

            @Override
            public void onSuccess(final Boolean success) {
                ViewGroup likesLayout = (ViewGroup) rootView.findViewById(R.id.layout_likes);
                updateLikesLayout(likesLayout, moment.getLikesInfo());
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    private void notifyMomentOwner(User currentUser) throws Exception {
        if (!moment.getUserId().equals(currentUser.getObjectId())) {
            CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.MOMENT_LIKE,
                    null, currentUser.getObjectId(), moment.getObjectId());
            UserInfo user = userStore.getUserInfoById(moment.getUserId());
            chatService.sendCmdMessage(user.getChatId(), cmdMessage);
        }
    }

    public void updateLikesLayout(ViewGroup likesLayout, List<UserInfo> likesInfo) {
        final ArrayList<UserInfo> likes = new ArrayList<>();
        likes.addAll(likesInfo);
        Collections.reverse(likes);
        final int size = likes.size();
        if (size > 0) {
            ViewGroup layoutLikingAvatars =
                    (ViewGroup) likesLayout.findViewById(R.id.layout_avatars);
            layoutLikingAvatars.removeAllViews();
            LayoutInflater inflater = activity.getLayoutInflater();
            final int displayWidth = DisplayUtils.getDisplayPixelWidth(activity);
            final float spaceForAvatars = displayWidth -
                    (activity.getResources().getDimensionPixelSize(R.dimen.margin_large) * 2);
            final float avatarSizeWithMargin =
                    activity.getResources().getDimensionPixelSize(R.dimen.avatar_sz_small) +
                    activity.getResources().getDimensionPixelSize(R.dimen.margin_small);
            final int maxAvatars = (int)(spaceForAvatars / avatarSizeWithMargin) - 1;
            int count = 0;
            TextView likesText = (TextView) likesLayout.findViewById(R.id.text_likes);
            likesText.setVisibility(View.INVISIBLE);
            for(UserInfo user: likes) {
                if (count < maxAvatars) {
                    AvatarImageView avatarImage = (AvatarImageView) inflater
                            .inflate(R.layout.avatar_thumbnail, layoutLikingAvatars, false);
                    avatarImage.setOnClickListener(new UserListener(activity, user.getObjectId()));
                    avatarImage.getFromUrl(user.getAvatarUrl());
                    layoutLikingAvatars.addView(avatarImage);
                    count++;
                } else {
                    likesText.setVisibility(View.VISIBLE);
                    likesText.setText(String.valueOf(size));
                    likesText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<String> users = new ArrayList<>(moment.getLikes());
                            final Intent intent = new Intent(activity, UserListActivity.class);
                            intent.putExtra(UserListActivity.INTENT_TITLE,
                                    activity.getString(R.string.label_users_like, size));
                            intent.putStringArrayListExtra(UserListActivity.INTENT_USER_LIST, users);
                            activity.startActivity(intent);
                        }
                    });
                    break;
                }
            }
            if (likesLayout.getVisibility() != View.VISIBLE) {
                Animation.fadeIn(likesLayout, Animation.Duration.SHORT);
            }
        } else {
            likesLayout.setVisibility(View.GONE);
        }
    }
}