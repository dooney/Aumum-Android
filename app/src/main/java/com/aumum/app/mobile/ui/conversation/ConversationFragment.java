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
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.events.NewChatMessageEvent;
import com.aumum.app.mobile.events.ResetChatUnreadEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.EMChatUtils;
import com.easemob.chat.EMConversation;
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
        List<EMConversation> emConversations = EMChatUtils.getAllConversations();
        for (final EMConversation emConversation: emConversations) {
            Conversation conversation = new Conversation(emConversation);
            UserInfo contact = userStore.getUserInfoByChatId(emConversation.getUserName());
            if (contact != null) {
                conversation.setContact(contact);
                result.add(conversation);
            }
        }
        return result;
    }

    @Subscribe
    public void onNewChatMessageEvent(NewChatMessageEvent event) {
        refresh(null);
    }
}