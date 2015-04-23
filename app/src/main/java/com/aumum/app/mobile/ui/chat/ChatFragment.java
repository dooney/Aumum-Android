package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.events.DeleteChatMessageEvent;
import com.aumum.app.mobile.events.GroupDeletedEvent;
import com.aumum.app.mobile.ui.group.GroupDetailsActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.StorageUtils;
import com.easemob.EMError;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.util.VoiceRecorder;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Inject Bus bus;

    private String id;
    private boolean isGroup;

    private ListView listView;

    private Button voiceButton;
    private Button keyboardButton;
    private ViewGroup pressToTalkLayout;
    private EditText chatText;
    private Button typeSelectButton;
    private Button sendButton;
    private View typeSelectionLayout;
    private View imageLayout;
    private ViewGroup recordingLayout;
    private TextView recordingHintText;
    private ImageView micImage;

    private final TextWatcher watcher = validationTextWatcher();
    private ChatMessagesAdapter adapter;
    private EMConversation conversation;
    private NewMessageBroadcastReceiver newMessageBroadcastReceiver;

    private VoiceRecorder voiceRecorder;
    private Drawable[] micImages;
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    private PowerManager.WakeLock wakeLock;

    private boolean isLoading;
    private boolean loadMore = true;
    private final int LIMIT_PER_LOAD = 20;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        id = intent.getStringExtra(ChatActivity.INTENT_ID);
        conversation = chatService.getConversation(id);
        conversation.resetUnreadMsgCount();
        int type = intent.getIntExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
        isGroup = type == ChatActivity.TYPE_GROUP;
        adapter = new ChatMessagesAdapter(getActivity(), bus, isGroup);
        adapter.addAll(conversation.getAllMessages());

        newMessageBroadcastReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(chatService.getNewMessageBroadcastAction());
        intentFilter.setPriority(NewMessageBroadcastReceiver.PRIORITY);
        getActivity().registerReceiver(newMessageBroadcastReceiver, intentFilter);

        micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02), getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04), getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06), getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08), getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10), getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12), getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14), };
        voiceRecorder = new VoiceRecorder(micImageHandler);
        wakeLock = ((PowerManager) getActivity()
                .getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "aumum");
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        int type = getActivity().getIntent().getIntExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
        isGroup = type == ChatActivity.TYPE_GROUP;
        if (!isGroup) {
            MenuItem more = menu.add(Menu.NONE, 0, Menu.NONE, null);
            more.setActionView(R.layout.menuitem_more);
            more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            View moreView = more.getActionView();
            ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
            moreIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() != null) {
                        showSingleChatActions();
                    }
                }
            });
        } else {
            MenuItem users = menu.add(Menu.NONE, 0, Menu.NONE, null);
            users.setActionView(R.layout.menuitem_users);
            users.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            View usersView = users.getActionView();
            ImageView usersIcon = (ImageView) usersView.findViewById(R.id.b_users);
            usersIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() != null) {
                        startGroupDetailsActivity();
                    }
                }
            });
        }
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
        listView.setSelection(adapter.getCount() - 1);

        voiceButton = (Button) view.findViewById(R.id.b_set_voice_mode);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                keyboardButton.setVisibility(View.VISIBLE);
                chatText.setVisibility(View.GONE);
                EditTextUtils.hideSoftInput(chatText);
                pressToTalkLayout.setVisibility(View.VISIBLE);
                typeSelectionLayout.setVisibility(View.GONE);
            }
        });
        keyboardButton = (Button) view.findViewById(R.id.b_set_keyboard_mode);
        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                voiceButton.setVisibility(View.VISIBLE);
                pressToTalkLayout.setVisibility(View.GONE);
                chatText.setVisibility(View.VISIBLE);
                EditTextUtils.showSoftInput(chatText, true);
            }
        });
        pressToTalkLayout = (ViewGroup) view.findViewById(R.id.layout_press_to_talk);
        pressToTalkLayout.setOnTouchListener(new PressToTalkListener());

        chatText = (EditText) view.findViewById(R.id.et_text);
        chatText.addTextChangedListener(watcher);
        chatText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    typeSelectionLayout.setVisibility(View.GONE);
                    listView.setSelection(adapter.getCount() - 1);
                }
            }
        });
        typeSelectButton = (Button) view.findViewById(R.id.b_type_select);
        typeSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboardButton.setVisibility(View.GONE);
                voiceButton.setVisibility(View.VISIBLE);
                pressToTalkLayout.setVisibility(View.GONE);
                chatText.setVisibility(View.VISIBLE);
                chatText.clearFocus();
                EditTextUtils.hideSoftInput(chatText);
                toggleTypeSelectionLayout();
                listView.clearFocus();
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.requestFocusFromTouch();
                        listView.setSelection(adapter.getCount() - 1);
                        listView.requestFocus();
                    }
                });
            }
        });
        sendButton = (Button) view.findViewById(R.id.b_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText();
            }
        });

        typeSelectionLayout = view.findViewById(R.id.layout_type_selection);
        imageLayout = view.findViewById(R.id.layout_image);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.INTENT_ACTION, ImagePickerActivity.ACTION_MULTIPLE_PICK);
                startActivityForResult(intent, Constants.RequestCode.IMAGE_PICKER_REQ_CODE);
            }
        });

        recordingLayout = (ViewGroup) view.findViewById(R.id.layout_recording);
        recordingHintText = (TextView) view.findViewById(R.id.text_recording_hint);
        micImage = (ImageView) view.findViewById(R.id.image_mic);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        VoicePlayClickListener.getInstance(getActivity()).stopPlayVoice();

        try {
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingLayout.setVisibility(View.GONE);
            }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.IMAGE_PICKER_REQ_CODE) {
            toggleTypeSelectionLayout();
            if (resultCode == Activity.RESULT_OK) {
                final String allPath[] = data.getStringArrayExtra(ImagePickerActivity.INTENT_ALL_PATH);
                new SafeAsyncTask<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        if (allPath != null) {
                            for (String path : allPath) {
                                EMMessage message = chatService.addImageMessage(id, isGroup, path);
                                conversation.addMessage(message);
                                preLoadMessageImage(message);
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected void onSuccess(Boolean success) throws Exception {
                        if (success) {
                            adapter.addAll(conversation.getAllMessages());
                        }
                    }
                }.execute();
            }
        } else if (requestCode == Constants.RequestCode.GET_GROUP_DETAILS_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        }
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
        if (typeSelectButton != null) {
            typeSelectButton.setVisibility(populated ? View.GONE : View.VISIBLE);
        }
        if (sendButton != null) {
            sendButton.setVisibility(populated ? View.VISIBLE : View.GONE);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void sendText() {
        String text = chatText.getText().toString();
        if (text.length() > 0) {
            EditTextUtils.hideSoftInput(chatText);
            chatText.setText(null);
            EMMessage message = chatService.addTextMessage(id, isGroup, text);
            conversation.addMessage(message);
            adapter.addAll(conversation.getAllMessages());
        }
    }

    private void sendVoice(String filePath, int length) {
        if (!(new File(filePath).exists())) {
            return;
        }
        EMMessage message = chatService.addVoiceMessage(id, isGroup, filePath, length);
        conversation.addMessage(message);
        adapter.addAll(conversation.getAllMessages());
    }

    private void preLoadMessageImage(EMMessage message) {
        if (message.getType() == EMMessage.Type.IMAGE) {
            ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
            String imageUri;
            if (message.direct == EMMessage.Direct.RECEIVE) {
                imageUri = imageBody.getRemoteUrl();
            } else {
                imageUri = "file:/" + imageBody.getLocalUrl();
            }
            ImageLoaderUtils.loadImage(imageUri);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (absListView.getFirstVisiblePosition() == 0 && !isLoading && loadMore) {
                    isLoading = true;
                    final List<EMMessage> messages = new ArrayList<EMMessage>();
                    new SafeAsyncTask<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            String beforeId = adapter.getItem(0).getMsgId();
                            if (!isGroup) {
                                messages.addAll(conversation.loadMoreMsgFromDB(beforeId, LIMIT_PER_LOAD));
                            } else {
                                messages.addAll(conversation.loadMoreGroupMsgFromDB(beforeId, LIMIT_PER_LOAD));
                            }
                            for (EMMessage message: messages) {
                                preLoadMessageImage(message);
                            }
                            return true;
                        }

                        @Override
                        protected void onSuccess(Boolean success) throws Exception {
                            if (messages.size() > 0) {
                                adapter.addAll(conversation.getAllMessages());
                                listView.setSelection(messages.size() - 1);
                            }
                            if (messages.size() < LIMIT_PER_LOAD) {
                                loadMore = false;
                            }
                            isLoading = false;
                        }
                    }.execute();
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                chatText.clearFocus();
                EditTextUtils.hideSoftInput(chatText);
                typeSelectionLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {

        public static final int PRIORITY = 5;

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            final String chatId = intent.getStringExtra("from");
            final String msgId = intent.getStringExtra("msgid");
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    userStore.getUserByChatId(chatId);
                    EMMessage message = chatService.getMessage(msgId);
                    preLoadMessageImage(message);
                    return true;
                }

                @Override
                protected void onSuccess(Boolean success) throws Exception {
                    adapter.addAll(conversation.getAllMessages());
                    conversation.resetUnreadMsgCount();
                }
            }.execute();
        }
    }

    private void toggleTypeSelectionLayout() {
        if (typeSelectionLayout.getVisibility() == View.GONE) {
            typeSelectionLayout.setVisibility(View.VISIBLE);
        } else {
            typeSelectionLayout.setVisibility(View.GONE);
        }
    }

    class PressToTalkListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!StorageUtils.isExitsSdCard()) {
                        Toaster.showShort(getActivity(), R.string.error_sdcard_needed_for_voice);
                        return false;
                    }
                    try {
                        view.setPressed(true);
                        wakeLock.acquire();
                        VoicePlayClickListener.getInstance(getActivity()).stopPlayVoice();
                        recordingLayout.setVisibility(View.VISIBLE);
                        recordingHintText.setText(getString(R.string.label_move_up_to_cancel));
                        voiceRecorder.startRecording(null, id, getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                        view.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null) {
                            voiceRecorder.discardRecording();
                        }
                        recordingLayout.setVisibility(View.GONE);
                        Toaster.showShort(getActivity(), R.string.error_recoding_failed);
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHintText.setText(getString(R.string.label_release_to_cancel));
                    } else {
                        recordingHintText.setText(getString(R.string.label_move_up_to_cancel));
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    view.setPressed(false);
                    recordingLayout.setVisibility(View.GONE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(), length);
                            } else if (length == EMError.INVALID_FILE) {
                                Toaster.showShort(getActivity(), R.string.error_recoding_failed);
                            } else {
                                Toaster.showShort(getActivity(), R.string.error_recoding_too_short);
                            }
                        } catch (Exception e) {
                            Ln.e(e);
                            Toaster.showShort(getActivity(), R.string.error_send_recoding_failed);
                        }

                    }
                    return true;
                default:
                    recordingLayout.setVisibility(View.GONE);
                    if (voiceRecorder != null) {
                        voiceRecorder.discardRecording();
                    }
                    return false;
            }
        }
    }

    private void showSingleChatActions() {
        final String options[] = getResources().getStringArray(R.array.label_single_chat_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                clearConversation();
                                break;
                            case 1:
                                reportUser(ReportActivity.TYPE_USER, id);
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startGroupDetailsActivity() {
        String groupId = conversation.getUserName();
        final Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
        intent.putExtra(GroupDetailsActivity.INTENT_GROUP_ID, groupId);
        startActivityForResult(intent, Constants.RequestCode.GET_GROUP_DETAILS_REQ_CODE);
    }

    private void clearConversation() {
        String userName = conversation.getUserName();
        chatService.clearConversation(userName);
        adapter.addAll(conversation.getAllMessages());
    }

    private void reportUser(String type, String id) {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, type);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, id);
        startActivity(intent);
    }

    @Subscribe
    public void onGroupDeletedEvent(GroupDeletedEvent event) {
        getActivity().finish();
    }

    @Subscribe
    public void onDeleteChatMessageEvent(final DeleteChatMessageEvent event) {
        new TextViewDialog(getActivity(),
                getString(R.string.info_confirm_delete_chat_message),
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        conversation.removeMessage(event.getMessageId());
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        adapter.addAll(conversation.getAllMessages());
                    }
                }).show();
    }
}
