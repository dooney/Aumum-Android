package com.aumum.app.mobile.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.ChatMessage;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.events.DeleteChatMessageEvent;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.ui.view.dialog.TextViewDialog;
import com.aumum.app.mobile.utils.Emoticons.EmoticonsUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.StorageUtils;
import com.aumum.app.mobile.utils.TuSdkUtils;
import com.easemob.EMError;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.util.VoiceRecorder;
import com.github.kevinsawicki.wishlist.Toaster;
import com.keyboard.XhsEmoticonsKeyBoardBar;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ChatFragment extends Fragment
        implements AbsListView.OnScrollListener,
                   XhsEmoticonsKeyBoardBar.KeyBoardBarViewListener,
                   TuSdkUtils.CameraListener,
                   TuSdkUtils.AlbumListener,
                   TuSdkUtils.EditListener {

    @Inject ChatService chatService;
    @Inject UserStore userStore;
    @Inject Bus bus;

    private String id;

    private XhsEmoticonsKeyBoardBar xhsEmoticonsKeyBoardBar;
    private ListView listView;
    private ViewGroup recordingLayout;
    private TextView recordingHintText;
    private ImageView micImage;
    private ProgressBar progressBar;

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
        adapter = new ChatMessagesAdapter(getActivity(), bus, chatService.getChatId());
        updateUI(conversation.getAllMessages(), -1);

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
        listView.setOnScrollListener(this);

        EmoticonsUtils.initEmoticonsDB(getActivity());
        xhsEmoticonsKeyBoardBar = (XhsEmoticonsKeyBoardBar) view.findViewById(R.id.kv_bar);
        xhsEmoticonsKeyBoardBar.setVideoVisibility(false);
        xhsEmoticonsKeyBoardBar.setBuilder(EmoticonsUtils.getBuilder(getActivity()));
        xhsEmoticonsKeyBoardBar.setOnKeyBoardBarViewListener(this);

        recordingLayout = (ViewGroup) view.findViewById(R.id.layout_recording);
        recordingHintText = (TextView) view.findViewById(R.id.text_recording_hint);
        micImage = (ImageView) view.findViewById(R.id.image_mic);

        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
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

    private void sendVoice(String filePath, int length) {
        if (!(new File(filePath).exists())) {
            return;
        }
        EMMessage message = chatService.addVoiceMessage(id, filePath, length);
        conversation.addMessage(message);
        updateUI(conversation.getAllMessages(), -1);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                break;
            case SCROLL_STATE_IDLE:
                if (absListView.getFirstVisiblePosition() == 0 && !isLoading && loadMore) {
                    isLoading = true;
                    final List<EMMessage> messages = new ArrayList<>();
                    new SafeAsyncTask<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            String beforeId = adapter.getItem(0).getMessage().getMsgId();
                            messages.addAll(conversation.loadMoreMsgFromDB(beforeId, LIMIT_PER_LOAD));
                            return true;
                        }

                        @Override
                        protected void onSuccess(Boolean success) throws Exception {
                            if (messages.size() > 0) {
                                updateUI(conversation.getAllMessages(), messages.size() - 1);
                            }
                            if (messages.size() < LIMIT_PER_LOAD) {
                                loadMore = false;
                            }
                            isLoading = false;
                        }
                    }.execute();
                }
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                xhsEmoticonsKeyBoardBar.hideAutoView();
                break;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void OnKeyBoardStateChange(int state, int height) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    @Override
    public void OnSendBtnClick(String msg) {
        EMMessage message = chatService.addTextMessage(id, msg);
        conversation.addMessage(message);
        updateUI(conversation.getAllMessages(), -1);
        xhsEmoticonsKeyBoardBar.clearEditText();
    }

    @Override
    public void OnVideoBtnPress(View view, MotionEvent motionEvent) {
        pressToTalk(view, motionEvent);
    }

    @Override
    public void OnMultimediaBtnClick() {
        showCameraOptions();
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {

        public static final int PRIORITY = 5;

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            final String msgId = intent.getStringExtra("msgid");
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    EMMessage message = chatService.getMessage(msgId);
                    if (message.getType() == EMMessage.Type.IMAGE) {
                        ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                        ImageLoaderUtils.loadImage(imageBody.getRemoteUrl());
                    }
                    return true;
                }

                @Override
                protected void onSuccess(Boolean success) throws Exception {
                    updateUI(conversation.getAllMessages(), -1);
                    conversation.resetUnreadMsgCount();
                }
            }.execute();
        }
    }

    private boolean pressToTalk(View view, MotionEvent event) {
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

    private void clearConversation() {
        String userName = conversation.getUserName();
        chatService.clearConversation(userName);
        updateUI(conversation.getAllMessages(), 0);
    }

    private void reportUser(String type, String id) {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, type);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, id);
        startActivity(intent);
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
                        updateUI(conversation.getAllMessages(), -1);
                    }
                }).show();
    }

    private void updateUI(final List<EMMessage> messages, final int position) {
        final List<ChatMessage> messageList = new ArrayList<>();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                for (EMMessage message: messages) {
                    UserInfo user = userStore.getUserInfoByChatId(message.getFrom());
                    if (user != null) {
                        ChatMessage chatMessage = new ChatMessage(user, message);
                        messageList.add(chatMessage);
                    }
                }
                return true;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(getActivity(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                adapter.addAll(messageList);
                if (listView != null && position < 0) {
                    listView.setSelection(messageList.size() - 1);
                } else {
                    listView.setSelection(position);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }

    private void showCameraOptions() {
        String options[] = getResources().getStringArray(R.array.label_camera_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                TuSdkUtils.camera(getActivity(), ChatFragment.this);
                                break;
                            case 1:
                                TuSdkUtils.album(getActivity(), ChatFragment.this);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void onPhotoResult(ImageSqlInfo imageSqlInfo) {
        TuSdkUtils.edit(getActivity(), imageSqlInfo, true, true, this);
    }

    @Override
    public void onCameraResult(ImageSqlInfo imageSqlInfo) {
        onPhotoResult(imageSqlInfo);
    }

    @Override
    public void onAlbumResult(ImageSqlInfo imageSqlInfo) {
        onPhotoResult(imageSqlInfo);
    }

    @Override
    public void onEditResult(File file) {
        String localUri = file.getAbsolutePath();
        EMMessage message = chatService.addImageMessage(id, localUri);
        conversation.addMessage(message);
        ImageLoaderUtils.loadImage(ImageLoaderUtils.getFullPath(localUri));
        updateUI(conversation.getAllMessages(), -1);
    }
}
