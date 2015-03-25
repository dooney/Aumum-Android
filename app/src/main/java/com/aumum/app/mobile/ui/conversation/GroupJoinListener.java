package com.aumum.app.mobile.ui.conversation;

import android.app.Activity;
import android.view.View;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

/**
 * Created by Administrator on 25/03/2015.
 */
public class GroupJoinListener implements View.OnClickListener {

    @Inject ChatService chatService;

    private Activity activity;
    private String groupId;

    public GroupJoinListener(Activity activity, String groupId) {
        this.activity = activity;
        this.groupId = groupId;
        Injector.inject(this);
    }

    @Override
    public void onClick(View view) {
        new EditTextDialog(activity,
                R.layout.dialog_edit_text_multiline,
                R.string.hint_join_group_reason,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        String reason = (String) value;
                        chatService.applyJoinGroup(groupId, reason);
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(activity, errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        Toaster.showShort(activity, R.string.info_join_group_sent);
                    }
                }).show();
    }
}
