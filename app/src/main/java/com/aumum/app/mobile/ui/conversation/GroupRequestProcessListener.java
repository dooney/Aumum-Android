package com.aumum.app.mobile.ui.conversation;

import android.app.Activity;
import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.GroupRequest;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.Arrays;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 25/03/2015.
 */
public class GroupRequestProcessListener implements View.OnClickListener {

    @Inject ChatService chatService;
    @Inject UserStore userStore;
    private Activity activity;
    private GroupRequest request;
    private OnProcessListener listener;

    public interface OnProcessListener {
        public void onStart();
        public void onException(Exception e);
        public void onSuccess(boolean approve);
    }

    public GroupRequestProcessListener(Activity activity,
                                       GroupRequest request,
                                       OnProcessListener listener) {
        this.activity = activity;
        this.request = request;
        this.listener = listener;
        Injector.inject(this);
    }

    @Override
    public void onClick(View view) {
        final String options[] = activity.getResources()
                .getStringArray(R.array.label_group_process_options);
        new ListViewDialog(activity, null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                accept(request);
                                break;
                            case 1:
                                showDeclineDialog();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void accept(final GroupRequest request) {
        if (listener != null) {
            listener.onStart();
        }
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                User user = request.getUser();
                String groupId = request.getGroupId();
                chatService.acceptGroupApplication(request.getUserId(), groupId);
                chatService.joinGroup(groupId, user.getChatId());
                String text = activity.getString(R.string.label_group_joint,
                        user.getScreenName());
                chatService.sendSystemMessage(groupId, true, text, null);
                CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.GROUP_JOIN,
                        null, null, groupId);
                chatService.sendCmdMessage(groupId, cmdMessage, true, null);
                request.setStatus(GroupRequest.STATUS_APPROVED);
                userStore.saveGroupRequest(request);
                return true;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(activity, cause.getMessage());
                    }
                }
                if (listener != null) {
                    listener.onException(e);
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                if (listener != null) {
                    listener.onSuccess(true);
                }
            }
        }.execute();
    }

    private void showDeclineDialog() {
        new EditTextDialog(activity,
                R.layout.dialog_edit_text_multiline,
                R.string.hint_decline_reason,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        String userId = request.getUserId();
                        String groupId = request.getGroupId();
                        String reason = (String) value;
                        chatService.declineGroupApplication(userId, groupId, reason);
                        request.setStatus(GroupRequest.STATUS_REJECTED);
                        userStore.saveGroupRequest(request);
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(activity, errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        if (listener != null) {
                            listener.onSuccess(false);
                        }
                    }
                }).show();
    }
}
