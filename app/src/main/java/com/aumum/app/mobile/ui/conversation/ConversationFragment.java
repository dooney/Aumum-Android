package com.aumum.app.mobile.ui.conversation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ConversationFragment extends ItemListFragment<Conversation> {

    @Inject ChatService chatService;
    @Inject UserStore userStore;

    private NewMessageBroadcastReceiver newMessageBroadcastReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        newMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(chatService.getNewMessageBroadcastAction());
        intentFilter.setPriority(NewMessageBroadcastReceiver.PRIORITY);
        getActivity().registerReceiver(newMessageBroadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            getData().clear();
            getData().addAll(getAllConversations());
            getListAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(newMessageBroadcastReceiver);
    }

    @Override
    protected ArrayAdapter<Conversation> createAdapter(List<Conversation> items) {
        return new ConversationsAdapter(getActivity(), items);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_conversations;
    }

    @Override
    protected List<Conversation> loadDataCore(Bundle bundle) throws Exception {
        return getAllConversations();
    }

    private List<Conversation> getAllConversations() throws Exception {
        List<Conversation> result = new ArrayList<Conversation>();
        List<EMConversation> emConversations = chatService.getAllConversations();
        for (EMConversation emConversation: emConversations) {
            Conversation conversation = new Conversation(emConversation);
            if (emConversation.isGroup()) {
                EMGroup emGroup = chatService.getGroupById(emConversation.getUserName());
                if (emGroup != null) {
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

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {

        public static final int PRIORITY = 4;

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            if (getListAdapter() != null) {
                getListAdapter().notifyDataSetChanged();
            }
        }
    }
}