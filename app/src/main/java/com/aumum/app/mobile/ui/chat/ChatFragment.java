package com.aumum.app.mobile.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ChatFragment extends Fragment
        implements AbsListView.OnScrollListener{

    @Inject ChatService chatService;
    @Inject UserStore userStore;
    @Inject ApiKeyProvider apiKeyProvider;

    private int type;
    private String id;

    private ListView listView;
    private EditText chatText;
    private Button typeSelectButton;
    private Button sendButton;

    private final TextWatcher watcher = validationTextWatcher();
    private ChatMessagesAdapter adapter;
    private EMConversation conversation;
    NewMessageBroadcastReceiver newMessageBroadcastReceiver;

    private boolean isLoading;
    private boolean loadMore = true;
    private final int LIMIT_PER_LOAD = 20;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        type = intent.getIntExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
        id = intent.getStringExtra(ChatActivity.INTENT_ID);
        conversation = chatService.getConversation(id);
        adapter = new ChatMessagesAdapter(getActivity(), conversation, userStore);

        newMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(chatService.getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        getActivity().registerReceiver(newMessageBroadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setSelector(android.R.color.transparent);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);
        int count = listView.getCount();
        if (count > 0) {
            listView.setSelection(count - 1);
        }

        chatText = (EditText) view.findViewById(R.id.et_text);
        chatText.addTextChangedListener(watcher);
        typeSelectButton = (Button) view.findViewById(R.id.b_type_select);
        sendButton = (Button) view.findViewById(R.id.b_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText();
            }
        });
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(chatText);
        typeSelectButton.setVisibility(populated ? View.GONE : View.VISIBLE);
        sendButton.setVisibility(populated ? View.VISIBLE : View.GONE);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void sendText() {
        String text = chatText.getText().toString();
        if (text.length() > 0) {
            EditTextUtils.hideSoftInput(chatText);
            chatText.setText(null);
            chatService.addTextMessage(apiKeyProvider.getAuthUserId(), id, type == ChatActivity.TYPE_GROUP, text);
            adapter.notifyDataSetChanged();
            listView.setSelection(listView.getCount() - 1);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (absListView.getFirstVisiblePosition() == 0 && !isLoading && loadMore) {
                    List<EMMessage> messages;
                    try {
                        String beforeId = adapter.getItem(0).getMsgId();
                        if (type == ChatActivity.TYPE_SINGLE) {
                            messages = conversation.loadMoreMsgFromDB(beforeId, LIMIT_PER_LOAD);
                        }
                        else {
                            messages = conversation.loadMoreGroupMsgFromDB(beforeId, LIMIT_PER_LOAD);
                        }
                    } catch (Exception e1) {
                        return;
                    }
                    if (messages.size() > 0) {
                        adapter.notifyDataSetChanged();
                        listView.setSelection(messages.size() - 1);
                        if (messages.size() != LIMIT_PER_LOAD)
                            loadMore = false;
                    } else {
                        loadMore = false;
                    }
                    isLoading = false;
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            adapter.notifyDataSetChanged();
            listView.setSelection(listView.getCount() - 1);
        }
    }
}
