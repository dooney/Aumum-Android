package com.aumum.app.mobile.ui.conversation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
import com.aumum.app.mobile.events.ShowConversationActionsEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
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
        Injector.inject(this);
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
        final ArrayList<String> chatIdList = new ArrayList<>();
        for (final EMConversation emConversation: emConversations) {
            Conversation conversation = new Conversation(emConversation);
            if (emConversation.isGroup()) {
                EMGroup emGroup = chatService.getGroupById(emConversation.getUserName());
                if (emGroup != null) {
                    for (EMMessage emMessage: emConversation.getAllMessages()) {
                        String chatId = emMessage.getFrom();
                        if (!chatIdList.contains(chatId)) {
                            chatIdList.add(chatId);
                        }
                    }
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
        if (chatIdList.size() > 0) {
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    userStore.getGroupUsers(chatIdList);
                    return true;
                }
            }.execute();
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

    @Subscribe
    public void onShowConversationActionsEvent(ShowConversationActionsEvent event) {
        showActionDialog();
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_conversation_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                showClearListDialog();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void showClearListDialog() {
        new TextViewDialog(getActivity(),
                getString(R.string.info_confirm_clear_conversation_list),
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        chatService.deleteAllConversation();
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        getData().clear();
                        getListAdapter().notifyDataSetChanged();
                    }
                }).show();
    }
}