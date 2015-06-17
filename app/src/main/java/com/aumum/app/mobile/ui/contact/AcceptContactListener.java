package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.EMChatUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

/**
 * Created by Administrator on 20/11/2014.
 */
public class AcceptContactListener implements View.OnClickListener {

    @Inject RestService restService;
    @Inject UserStore userStore;

    private String userId;
    private SafeAsyncTask<Boolean> task;

    protected OnActionListener onActionListener;

    protected OnProgressListener onProgressListener;

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public interface OnActionListener {
        void onAcceptContactSuccess();
        void onAcceptContactFailed();
    }

    public interface OnProgressListener {
        void onAcceptContactStart();
        void onAcceptContactFinish();
    }

    public AcceptContactListener(String userId) {
        Injector.inject(this);
        this.userId = userId;
    }

    @Override
    public void onClick(View view) {
        acceptContact(view.getContext());
    }

    private void acceptContact(final Context context) {
        if (task != null) {
            return;
        }
        if (onProgressListener != null) {
            onProgressListener.onAcceptContactStart();
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                EMChatUtils.acceptInvitation(userId);
                User currentUser = userStore.getCurrentUser();
                restService.addContact(currentUser.getObjectId(), userId);
                currentUser.addContact(userId);
                userStore.save(currentUser);

                String text = context.getString(R.string.info_invitation_accepted_and_start_chatting);
                UserInfo user = userStore.getUserInfoById(userId);
                EMChatUtils.sendSystemMessage(user.getChatId(), text);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if (onActionListener != null) {
                    onActionListener.onAcceptContactFailed();
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (onActionListener != null) {
                    onActionListener.onAcceptContactSuccess();
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (onProgressListener != null) {
                    onProgressListener.onAcceptContactFinish();
                }
                task = null;
            }
        };
        task.execute();
    }
}
