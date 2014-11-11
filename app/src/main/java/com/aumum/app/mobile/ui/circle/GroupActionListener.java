package com.aumum.app.mobile.ui.circle;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
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
public class GroupActionListener implements View.OnClickListener {

    @Inject ChatService chatService;

    private Activity activity;
    private EMGroup group;
    private SafeAsyncTask<Boolean> task;

    protected OnProgressListener onProgressListener;

    public static interface OnProgressListener {
        public void onActionStart();
        public void onActionSuccess(EMGroup group);
        public void onActionFinish();
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public GroupActionListener(Activity activity, EMGroup group) {
        Injector.inject(this);
        this.activity = activity;
        this.group = group;
    }

    @Override
    public void onClick(final View view) {
        if (onProgressListener != null) {
            onProgressListener.onActionStart();
        }
        final String label = ((TextView)view).getText().toString();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (group.getMembers().contains(chatService.getCurrentUser())) {

                } else {
                    if (group.isMembersOnly()) {
                        chatService.applyJoinToGroup(group.getGroupId());
                    } else {
                        chatService.joinGroup(group.getGroupId());
                    }
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                    Toaster.showShort(activity, activity.getString(R.string.error_group_action, label));
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                String message;
                if (group.isMembersOnly()) {
                    message = activity.getString(R.string.info_group_action_sent);
                } else {
                    message = activity.getString(R.string.info_group_action, label, group.getGroupName());
                }
                Toaster.showShort(activity, message);
                if (onProgressListener != null) {
                    onProgressListener.onActionSuccess(group);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (onProgressListener != null) {
                    onProgressListener.onActionFinish();
                }
                task = null;
            }
        };
        task.execute();
    }
}
