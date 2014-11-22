package com.aumum.app.mobile.ui.circle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
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
import com.aumum.app.mobile.utils.DialogUtils;
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
public class CircleFragment extends ItemListFragment<Conversation> {

    @Inject ChatService chatService;
    @Inject UserStore userStore;

    private NewMessageBroadcastReceiver newMessageBroadcastReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        newMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(chatService.getNewMessageBroadcastAction());
        intentFilter.setPriority(4);
        getActivity().registerReceiver(newMessageBroadcastReceiver, intentFilter);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        SubMenu search = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.hint_search_circle));
        MenuItem item = search.getItem();
        item.setIcon(R.drawable.ic_fa_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        String searchOptions[] = getResources().getStringArray(R.array.label_search_circle);
        DialogUtils.showDialog(getActivity(), searchOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        final Intent intent = new Intent(getActivity(), SearchCircleActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        });
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_circle, null);
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
        return R.string.error_load_circles;
    }

    @Override
    protected List<Conversation> loadDataCore(Bundle bundle) throws Exception {
        return getAllConversations();
    }

    @Override
    protected void handleLoadResult(List<Conversation> result) {
        try {
            if (result != null) {
                getData().clear();
                getData().addAll(result);
                getListAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private List<Conversation> getAllConversations() throws Exception {
        List<Conversation> result = new ArrayList<Conversation>();
        List<EMConversation> emConversations = chatService.getAllConversations();
        for (EMConversation emConversation: emConversations) {
            Conversation conversation = new Conversation(emConversation);
            if (emConversation.isGroup()) {
                EMGroup emGroup = chatService.getGroupById(emConversation.getUserName());
                Group group = new Group(emGroup.getGroupId(), emGroup.getGroupName());
                conversation.setGroup(group);
            } else {
                User contact = userStore.getUserByChatId(emConversation.getUserName());
                conversation.setContact(contact);
            }
            result.add(conversation);
        }
        return result;
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            getListAdapter().notifyDataSetChanged();
        }
    }
}