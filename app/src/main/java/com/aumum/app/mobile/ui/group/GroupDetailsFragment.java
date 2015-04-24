package com.aumum.app.mobile.ui.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.base.ProgressListener;
import com.aumum.app.mobile.ui.contact.ContactPickerActivity;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.user.UserListAdapter;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.EMCallBack;
import com.easemob.chat.EMGroup;
import com.github.kevinsawicki.wishlist.Toaster;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupDetailsFragment extends ItemListFragment<User> {

    @Inject ChatService chatService;
    @Inject UserStore userStore;

    private String groupId;
    private User currentUser;
    private EMGroup group;
    private SafeAsyncTask<Boolean> task;
    private ProgressListener progressListener;
    private MenuItem moreMenu;

    private final int ADD_USERS_REQ_CODE = 100;
    private final int REMOVE_USERS_REQ_CODE = 101;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        groupId = intent.getStringExtra(GroupDetailsActivity.INTENT_GROUP_ID);

        progressListener = (ProgressListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_details, null);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        moreMenu = menu.add(Menu.NONE, 0, Menu.NONE, null);
        moreMenu.setActionView(R.layout.menuitem_more);
        moreMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = moreMenu.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null && group != null) {
                    showActionDialog();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_USERS_REQ_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> selectedContacts = data.getStringArrayListExtra(
                    ContactPickerActivity.INTENT_SELECTED_CONTACTS);
            if (selectedContacts != null) {
                addUsers(selectedContacts);
            }
        } else if (requestCode == REMOVE_USERS_REQ_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> selectedMembers = data.getStringArrayListExtra(
                    GroupMemberPickerActivity.INTENT_SELECTED_MEMBERS);
            if (selectedMembers != null) {
                removeUsers(selectedMembers);
            }
        }
    }

    @Override
    protected ArrayAdapter<User> createAdapter(List<User> items) {
        return new UserListAdapter(getActivity(), items);
    }

    @Override
    protected List<User> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        group = chatService.getGroupFromServer(groupId);
        final List<String> members = group.getMembers();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!members.contains(currentUser.getChatId())) {
                    moreMenu.setVisible(false);
                }
            }
        });
        return userStore.getGroupUsers(members);
    }

    @Override
    protected void handleLoadResult(List<User> result) {
        super.handleLoadResult(result);
        setTitle(group.getMembers().size());
    }

    private void setTitle(int groupSize) {
        String title = String.format("%s (%d)",
                getString(R.string.title_activity_group_details), groupSize);
        getActivity().setTitle(title);
    }

    private void showActionDialog() {
        if (group.getOwner().equals(currentUser.getChatId())) {
            showOwnerActionDialog();
        } else {
            showMemberActionDialog();
        }
    }

    private void quit() {
        String text = getActivity().getString(R.string.label_group_quit, currentUser.getScreenName());
        progressListener.setMessage(R.string.info_submitting_quit_group);
        progressListener.showProgress();
        chatService.sendSystemMessage(groupId, true, text, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (task != null) {
                    progressListener.hideProgress();
                    return;
                }
                task = new SafeAsyncTask<Boolean>() {
                    public Boolean call() throws Exception {
                        chatService.quitGroup(groupId, currentUser.getChatId());
                        return true;
                    }

                    @Override
                    protected void onException(final Exception e) throws RuntimeException {
                        if (!(e instanceof RetrofitError)) {
                            final Throwable cause = e.getCause() != null ? e.getCause() : e;
                            if (cause != null) {
                                Toaster.showShort(getActivity(), cause.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onSuccess(final Boolean success) {
                        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.GROUP_QUIT,
                                null, currentUser.getObjectId(), groupId);
                        chatService.sendCmdMessage(groupId, cmdMessage, true, null);
                        chatService.deleteGroupConversation(groupId);
                        Toaster.showShort(getActivity(), getActivity().getString(R.string.info_group_quit));
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }

                    @Override
                    protected void onFinally() throws RuntimeException {
                        progressListener.hideProgress();
                        task = null;
                    }
                };
                task.execute();
            }

            @Override
            public void onError(int i, String message) {
                progressListener.hideProgress();
                Toaster.showShort(getActivity(), message);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void delete() {
        if (task != null) {
            return;
        }
        progressListener.setMessage(R.string.info_deleting_group);
        progressListener.showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                chatService.deleteGroup(groupId);
                userStore.deleteGroupRequests(groupId);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if (!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if (cause != null) {
                        Toaster.showShort(getActivity(), cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                chatService.deleteGroupConversation(groupId);
                Toaster.showShort(getActivity(), R.string.info_group_deleted);
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                progressListener.hideProgress();
                task = null;
            }
        };
        task.execute();
    }

    private void showOwnerActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_group_owner_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 0:
                        startAddUsersActivity();
                        break;
                    case 1:
                        startRemoveUsersActivity();
                        break;
                    case 2:
                        delete();
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void showMemberActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_group_member_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 0:
                        report();
                        break;
                    case 1:
                        quit();
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void report() {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_GROUP);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, groupId);
        startActivity(intent);
    }

    private void startAddUsersActivity() {
        final Intent intent = new Intent(getActivity(), ContactPickerActivity.class);
        startActivityForResult(intent, ADD_USERS_REQ_CODE);
    }

    private void addUsers(final ArrayList<String> contacts) {
        final List<User> userList = getData();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (final String contactId : contacts) {
                    if (!group.getMembers().contains(contactId)) {
                        User user = userStore.getUserById(contactId);
                        userList.add(user);
                    }
                }
                return true;
            }

            @Override
            public void onSuccess(final Boolean success) {
                setTitle(userList.size());
                getListAdapter().notifyDataSetChanged();
            }
        }.execute();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (final String contactId: contacts) {
                    if (!group.getMembers().contains(contactId)) {
                        chatService.addUserToGroup(groupId, contactId);
                        User user = userStore.getUserById(contactId);
                        String text = currentUser.getScreenName() + "邀请" +
                                user.getScreenName() + "加入群组";
                        chatService.sendSystemMessage(groupId, true, text, null);
                        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.GROUP_JOIN,
                                null, contactId, groupId);
                        chatService.sendCmdMessage(groupId, cmdMessage, true, null);
                    }
                }
                return true;
            }
        }.execute();
    }

    private void startRemoveUsersActivity() {
        final Intent intent = new Intent(getActivity(), GroupMemberPickerActivity.class);
        Gson gson = new Gson();
        String allMembers = gson.toJson(getData());
        intent.putExtra(GroupMemberPickerActivity.INTENT_ALL_MEMBERS, allMembers);
        startActivityForResult(intent, REMOVE_USERS_REQ_CODE);
    }

    private void removeUsers(final ArrayList<String> contacts) {
        final List<User> userList = getData();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (final String contactId : contacts) {
                    if (!group.getMembers().contains(contactId)) {
                        for (Iterator<User> it = userList.iterator(); it.hasNext();) {
                            User user = it.next();
                            if (user.getObjectId().equals(contactId)) {
                                it.remove();
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public void onSuccess(final Boolean success) {
                setTitle(userList.size());
                getListAdapter().notifyDataSetChanged();
            }
        }.execute();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (final String contactId: contacts) {
                    chatService.removeUserFromGroup(groupId, contactId);
                    User user = userStore.getUserById(contactId);
                    String text = user.getScreenName() + "已被移出群组";
                    chatService.sendSystemMessage(groupId, true, text, null);
                    CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.GROUP_QUIT,
                            null, contactId, groupId);
                    chatService.sendCmdMessage(groupId, cmdMessage, true, null);
                }
                return true;
            }
        }.execute();
    }
}
