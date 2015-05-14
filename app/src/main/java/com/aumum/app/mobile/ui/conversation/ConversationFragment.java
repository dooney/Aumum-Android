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
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.events.GroupDeletedEvent;
import com.aumum.app.mobile.events.NewChatMessageEvent;
import com.aumum.app.mobile.events.ResetChatUnreadEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.contact.ContactActivity;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
    @Inject Bus bus;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem users = menu.add(Menu.NONE, 0, Menu.NONE, null);
        users.setActionView(R.layout.menuitem_users);
        users.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View usersView = users.getActionView();
        ImageView usersIcon = (ImageView) usersView.findViewById(R.id.b_users);
        usersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    final Intent intent = new Intent(getActivity(), ContactActivity.class);
                    startActivity(intent);
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
                    Group group = new Group(emGroup.getGroupId(), emGroup.getGroupName());
                    conversation.setGroup(group);
                    result.add(conversation);
                }
            } else {
                UserInfo contact = userStore.getUserInfoByChatId(emConversation.getUserName());
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
}