package com.aumum.app.mobile.ui.conversation;

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
import com.aumum.app.mobile.core.model.Conversation;
import com.aumum.app.mobile.core.model.Group;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.events.GroupDeletedEvent;
import com.aumum.app.mobile.events.NewChatMessageEvent;
import com.aumum.app.mobile.events.ResetChatUnreadEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMMessage;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ConversationFragment extends ItemListFragment<Conversation> {

    @Inject ChatService chatService;
    @Inject UserStore userStore;
    @Inject Bus bus;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem more = menu.add(Menu.NONE, 0, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    showActionDialog();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(null);
        bus.register(this);
        bus.post(new ResetChatUnreadEvent());
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    protected ArrayAdapter<Conversation> createAdapter(List<Conversation> items) {
        return new ConversationsAdapter(getActivity(), items);
    }

    @Override
    protected List<Conversation> loadDataCore(Bundle bundle) throws Exception {
        return getAllConversations();
    }

    private List<Conversation> getAllConversations() throws Exception {
        List<Conversation> result = new ArrayList<Conversation>();
        List<EMConversation> emConversations = chatService.getAllConversations();
        for (final EMConversation emConversation: emConversations) {
            Conversation conversation = new Conversation(emConversation);
            if (emConversation.isGroup()) {
                EMGroup emGroup = chatService.getGroupById(emConversation.getUserName());
                if (emGroup != null) {
                    new SafeAsyncTask<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            for (EMMessage emMessage: emConversation.getAllMessages()) {
                                userStore.getUserByChatId(emMessage.getFrom());
                            }
                            return true;
                        }
                    }.execute();
                    Group group = new Group(emGroup.getGroupId(), emGroup.getGroupName());
                    conversation.setGroup(group);
                    result.add(conversation);
                }
            } else {
                User contact = userStore.getUserByChatId(emConversation.getUserName());
                if (contact != null) {
                    conversation.setContact(contact);
                    result.add(conversation);
                }
            }
        }
        return result;
    }

    @Subscribe
    public void onGroupDeletedEvent(GroupDeletedEvent event) {
        refresh(null);
    }

    @Subscribe
    public void onNewChatMessageEvent(NewChatMessageEvent event) {
        refresh(null);
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_conversation_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                showNewGroupDialog();
                                break;
                            case 1:
                                showSearchGroupDialog();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void showNewGroupDialog() {
        new EditTextDialog(getActivity(),
                R.layout.dialog_edit_text,
                R.string.hint_group_name,
                new ConfirmDialog.OnConfirmListener() {
            @Override
            public void call(Object value) throws Exception {
                String groupName = (String) value;
                User user = userStore.getCurrentUser();
                EMGroup group = chatService.createGroup(groupName);
                chatService.addGroupMember(group.getGroupId(), user.getChatId());
                String groupCreatedText = getString(R.string.label_group_created,
                        user.getScreenName());
                chatService.sendSystemMessage(group.getGroupId(),
                        true, groupCreatedText, null);
            }

            @Override
            public void onException(String errorMessage) {
                Toaster.showShort(getActivity(), errorMessage);
            }

            @Override
            public void onSuccess(Object value) {
                refresh(null);
            }
        }).show();
    }

    private void showSearchGroupDialog() {
        new EditTextDialog(getActivity(),
                R.layout.dialog_edit_text,
                R.string.hint_group_name,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        final String groupName = (String) value;
                        List<EMGroupInfo> groups = chatService.getPublicGroups();
                        final ArrayList<String> groupList = new ArrayList<>();
                        for(EMGroupInfo groupInfo: groups) {
                            if (groupInfo.getGroupName().contains(groupName)) {
                                groupList.add(groupInfo.getGroupId());
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (groupList.size() > 0) {
                                    startGroupListActivity(groupList);
                                } else {
                                    Toaster.showShort(getActivity(), R.string.info_no_group_found);
                                }
                            }
                        });
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                    }
                }).show();
    }

    private void startGroupListActivity(ArrayList<String> groupList) {
        final Intent intent = new Intent(getActivity(), GroupListActivity.class);
        intent.putStringArrayListExtra(GroupListActivity.INTENT_GROUP_LIST, groupList);
        startActivity(intent);
    }
}