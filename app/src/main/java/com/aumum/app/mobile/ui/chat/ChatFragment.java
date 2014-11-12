package com.aumum.app.mobile.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.utils.EditTextUtils;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ChatFragment extends Fragment {

    @Inject ChatService chatService;

    private int type;
    private String id;

    private ListView listView;
    private EditText chatText;
    private Button typeSelectButton;
    private Button sendButton;

    private final TextWatcher watcher = validationTextWatcher();
    private ChatMessagesAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        type = intent.getIntExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
        id = intent.getStringExtra(ChatActivity.INTENT_ID);
        adapter = new ChatMessagesAdapter(getActivity(), chatService.getConversation(id));
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
        listView.setAdapter(adapter);

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
            chatService.addTextMessage(id, type == ChatActivity.TYPE_GROUP, text);
            adapter.notifyDataSetChanged();
        }
    }
}
