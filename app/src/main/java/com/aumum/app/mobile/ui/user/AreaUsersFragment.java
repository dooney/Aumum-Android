package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.contact.AddContactListener;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.EditTextDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 15/01/2015.
 */
public class AreaUsersFragment extends ItemListFragment<UserInfo>
        implements AddContactListener {

    @Inject UserStore userStore;
    @Inject ChatService chatService;

    private String area;
    private String userId;
    private boolean shouldNotify;
    private User currentUser;
    private int usersCount;

    private AreaUsersAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        area = intent.getStringExtra(AreaUsersActivity.INTENT_AREA);
        userId = intent.getStringExtra(AreaUsersActivity.INTENT_USER_ID);
        shouldNotify = intent.getBooleanExtra(AreaUsersActivity.INTENT_SHOULD_NOTIFY, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_area_users, null);
    }

    @Override
    protected ArrayAdapter<UserInfo> createAdapter(List<UserInfo> items) {
        adapter = new AreaUsersAdapter(getActivity(), items, this);
        return adapter;
    }

    @Override
    protected List<UserInfo> loadDataCore(Bundle bundle) throws Exception {
        if (userId != null) {
            currentUser = userStore.getUserByIdFromServer(userId);
        } else {
            currentUser = userStore.getCurrentUser();
        }
        adapter.setCurrentUser(currentUser);
        List<UserInfo> users = userStore.getListByArea(currentUser.getObjectId(), area);
        usersCount = users.size();
        if (usersCount > 0 && shouldNotify) {
            notifyAreaUsers(users);
        }
        return users;
    }

    @Override
    protected void handleLoadResult(List<UserInfo> result) {
        super.handleLoadResult(result);
        String title = getString(R.string.info_area_users_found, area, usersCount);
        getActivity().setTitle(title);
    }

    @Override
    public void onAddContact(final String contactId) {
        EditTextDialog dialog = new EditTextDialog(getActivity(),
                R.layout.dialog_edit_text_multiline,
                R.string.hint_hello,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        String hello = (String) value;
                        chatService.addContact(contactId, hello);
                        Thread.sleep(1000);
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        Toaster.showShort(getActivity(), R.string.info_add_contact_sent);
                    }
                });
        String hello = getString(R.string.label_hello, currentUser.getScreenName());
        EditText valueText = dialog.getValueText();
        valueText.setText(hello);
        valueText.setSelection(valueText.getText().length());
        dialog.show();
    }

    private void notifyAreaUsers(final List<UserInfo> users) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (UserInfo user: users) {
                    String title = getString(R.string.info_new_area_user_message);
                    String content = getString(R.string.info_welcome_new_user, currentUser.getScreenName());
                    CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.USER_NEW,
                            title, content, currentUser.getObjectId());
                    chatService.sendCmdMessage(user.getChatId(), cmdMessage, false, null);
                }
                return true;
            }
        }.execute();
    }
}
