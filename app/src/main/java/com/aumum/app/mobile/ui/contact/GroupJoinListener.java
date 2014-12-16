package com.aumum.app.mobile.ui.contact;

import android.app.Activity;
import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.chat.EMGroup;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 10/11/2014.
 */
public class GroupJoinListener implements View.OnClickListener {

    @Inject ChatService chatService;
    @Inject UserStore userStore;

    private User currentUser;
    private Activity activity;
    private EMGroup group;
    private SafeAsyncTask<Boolean> task;

    protected OnProgressListener onProgressListener;

    public static interface OnProgressListener {
        public void onJoinStart();
        public void onJoinSuccess(EMGroup group);
        public void onJoinFinish();
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public GroupJoinListener(Activity activity, EMGroup group) {
        Injector.inject(this);
        this.activity = activity;
        this.group = group;
    }

    @Override
    public void onClick(final View view) {
        if (task != null) {
            return;
        }
        if (onProgressListener != null) {
            onProgressListener.onJoinStart();
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (group.isMembersOnly()) {
                    chatService.applyJoinToGroup(group.getGroupId());
                } else {
                    chatService.joinGroup(group.getGroupId());
                }
                currentUser = userStore.getCurrentUser();
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                    Toaster.showShort(activity, R.string.error_join_group);
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                String message;
                if (group.isMembersOnly()) {
                    message = activity.getString(R.string.info_group_application_sent);
                } else {
                    group.addMember(currentUser.getChatId());
                    message = activity.getString(R.string.info_group_joint, group.getGroupName());
                    String text = activity.getString(R.string.label_group_joint, currentUser.getScreenName());
                    chatService.sendSystemMessage(group.getGroupId(), true, text, null);
                }
                Toaster.showShort(activity, message);
                if (onProgressListener != null) {
                    onProgressListener.onJoinSuccess(group);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (onProgressListener != null) {
                    onProgressListener.onJoinFinish();
                }
                task = null;
            }
        };
        task.execute();
    }
}
