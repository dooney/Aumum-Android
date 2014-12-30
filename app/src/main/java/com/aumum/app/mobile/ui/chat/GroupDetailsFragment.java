package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.DialogInterface;
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

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.user.UserListAdapter;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.EMCallBack;
import com.easemob.chat.EMGroup;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        groupId = intent.getStringExtra(GroupDetailsActivity.INTENT_GROUP_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_details, null);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected ArrayAdapter<User> createAdapter(List<User> items) {
        return new UserListAdapter(getActivity(), items);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_group_details;
    }

    @Override
    protected List<User> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        group = chatService.getGroupById(groupId);
        List<User> members = new ArrayList<User>();
        for (String chatId: group.getMembers()) {
            User user = userStore.getUserByChatId(chatId);
            members.add(user);
        }
        return members;
    }

    @Override
    protected void handleLoadResult(List<User> result) {
        super.handleLoadResult(result);
        String title = String.format("%s (%d)",
                getString(R.string.title_activity_group_details), group.getMembers().size());
        getActivity().setTitle(title);
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_group_actions);
        DialogUtils.showDialog(getActivity(), options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                break;
                            case 1:
                                quit();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void quit() {
        try {
            String text = getActivity().getString(R.string.label_group_quit, currentUser.getScreenName());
            showProgress();
            chatService.sendSystemMessage(groupId, true, text, new EMCallBack() {
                @Override
                public void onSuccess() {
                    if (task != null) {
                        hideProgress();
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
                                    Ln.e(e.getCause(), cause.getMessage());
                                }
                                Toaster.showShort(getActivity(), R.string.error_quit_group);
                            }
                        }

                        @Override
                        public void onSuccess(final Boolean success) {
                            CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.GROUP_QUIT,
                                    null, null, groupId);
                            chatService.sendCmdMessage(groupId, cmdMessage, true, null);
                            chatService.deleteGroupConversation(groupId);
                            String message = getActivity().getString(R.string.info_group_quit);
                            Toaster.showShort(getActivity(), message);
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }

                        @Override
                        protected void onFinally() throws RuntimeException {
                            hideProgress();
                            task = null;
                        }
                    };
                    task.execute();
                }

                @Override
                public void onError(int i, String s) {
                    hideProgress();
                    Toaster.showShort(getActivity(), R.string.error_quit_group);
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
