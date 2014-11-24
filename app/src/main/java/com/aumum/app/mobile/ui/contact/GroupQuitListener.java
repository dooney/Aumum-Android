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
import com.easemob.EMCallBack;
import com.easemob.chat.EMGroup;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 24/11/2014.
 */
public class GroupQuitListener implements View.OnClickListener {

    @Inject ChatService chatService;
    @Inject UserStore userStore;

    private Activity activity;
    private EMGroup group;
    private SafeAsyncTask<Boolean> task;

    protected OnProgressListener onProgressListener;

    public static interface OnProgressListener {
        public void onQuitStart();
        public void onQuitSuccess(EMGroup group);
        public void onQuitFinish();
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public GroupQuitListener(Activity activity, EMGroup group) {
        Injector.inject(this);
        this.activity = activity;
        this.group = group;
    }

    @Override
    public void onClick(final View view) {
        if (onProgressListener != null) {
            onProgressListener.onQuitStart();
        }
        try {
            final User currentUser = userStore.getCurrentUser();
            String text = activity.getString(R.string.label_group_quit, currentUser.getScreenName());
            chatService.sendSystemMessage(group.getGroupId(), true, text, new EMCallBack() {
                @Override
                public void onSuccess() {
                    task = new SafeAsyncTask<Boolean>() {
                        public Boolean call() throws Exception {
                            chatService.quitGroup(group.getGroupId());
                            return true;
                        }

                        @Override
                        protected void onException(final Exception e) throws RuntimeException {
                            if (!(e instanceof RetrofitError)) {
                                final Throwable cause = e.getCause() != null ? e.getCause() : e;
                                if (cause != null) {
                                    Ln.e(e.getCause(), cause.getMessage());
                                }
                                Toaster.showShort(activity, R.string.error_quit_group);
                            }
                        }

                        @Override
                        public void onSuccess(final Boolean success) {
                            group.removeMember(currentUser.getChatId());
                            chatService.deleteGroupConversation(group.getGroupId());
                            String message = activity.getString(R.string.info_group_quit, group.getGroupName());
                            Toaster.showShort(activity, message);
                            if (onProgressListener != null) {
                                onProgressListener.onQuitSuccess(group);
                            }
                        }

                        @Override
                        protected void onFinally() throws RuntimeException {
                            if (onProgressListener != null) {
                                onProgressListener.onQuitFinish();
                            }
                            task = null;
                        }
                    };
                    task.execute();
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        } catch (Exception e) {
            Ln.e(e);
        }
    }
}
