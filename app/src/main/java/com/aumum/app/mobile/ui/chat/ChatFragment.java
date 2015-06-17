package com.aumum.app.mobile.ui.chat;

import android.content.Intent;
import android.os.Bundle;
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

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.ChatMessage;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.events.DeleteChatMessageEvent;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.ui.view.dialog.TextViewDialog;
import com.aumum.app.mobile.utils.EMChatUtils;
import com.aumum.app.mobile.utils.Emoticons.EmoticonsUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.TuSdkUtils;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
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
                   TuSdkUtils.EditListener,
                   EMEventListener {

    @Inject UserStore userStore;
    @Inject Bus bus;

    private String id;

    private XhsEmoticonsKeyBoardBar xhsEmoticonsKeyBoardBar;
    private ListView listView;
    private ProgressBar progressBar;

    private ChatMessagesAdapter adapter;
    private EMConversation conversation;

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
        conversation = EMChatUtils.getConversation(id);
        conversation.markAllMessagesAsRead();
        adapter = new ChatMessagesAdapter(getActivity(), bus, EMChatUtils.getChatId());
        updateUI(conversation.getAllMessages(), -1);
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

        progressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        EMChatUtils.pushActivity(getActivity());
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventNewCMDMessage
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        EMChatUtils.popActivity(getActivity());
        super.onStop();
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
        EMMessage message = EMChatUtils.addTextMessage(id, msg);
        conversation.addMessage(message);
        updateUI(conversation.getAllMessages(), -1);
        xhsEmoticonsKeyBoardBar.clearEditText();
    }

    @Override
    public void OnVideoBtnPress(View view, MotionEvent motionEvent) {
    }

    @Override
    public void OnMultimediaBtnClick() {
        showCameraOptions();
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: {
                final EMMessage message = (EMMessage) event.getData();
                handleNewMessage(message);
            }
                break;
            case EventNewCMDMessage: {
                final EMMessage message = (EMMessage) event.getData();
                handleNewCmdMessage(message);
            }
                break;
            default:
                break;
        }
    }

    private void handleNewMessage(final EMMessage message) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (message.getType() == EMMessage.Type.IMAGE) {
                    ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                    ImageLoaderUtils.loadImage(imageBody.getRemoteUrl());
                }
                return true;
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                updateUI(conversation.getAllMessages(), -1);
                conversation.markAllMessagesAsRead();
            }
        }.execute();
    }

    private void handleNewCmdMessage(final EMMessage message) {
        CmdMessage cmdMessage = EMChatUtils.getCmdMessage(message);
        if (cmdMessage != null) {
            EMChatUtils.pushCmdMessageQueue(cmdMessage);
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
        EMChatUtils.clearConversation(userName);
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
                        message.setAttribute("screenName", user.getScreenName());
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
        EMMessage message = EMChatUtils.addImageMessage(id, localUri);
        conversation.addMessage(message);
        ImageLoaderUtils.loadImage(ImageLoaderUtils.getFullPath(localUri));
        updateUI(conversation.getAllMessages(), -1);
    }
}
