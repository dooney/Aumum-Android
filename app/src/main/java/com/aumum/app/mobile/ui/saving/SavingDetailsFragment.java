package com.aumum.app.mobile.ui.saving;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.SavingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.Saving;
import com.aumum.app.mobile.core.model.SavingComment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.service.ShareService;
import com.aumum.app.mobile.events.AddSavingCommentEvent;
import com.aumum.app.mobile.events.AddSavingCommentFinishedEvent;
import com.aumum.app.mobile.events.ReplySavingCommentEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.base.ProgressListener;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.like.LikesLayoutListener;
import com.aumum.app.mobile.ui.saving.SavingCommentsFragment;
import com.aumum.app.mobile.ui.saving.SavingDetailsActivity;
import com.aumum.app.mobile.ui.saving.SavingLikeListener;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingDetailsFragment extends LoaderFragment<Saving> {

    @Inject Bus bus;
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject UserStore userStore;
    @Inject SavingStore savingStore;
    @Inject RestService restService;
    @Inject ChatService chatService;
    private ShareService shareService;

    private String savingId;
    private Saving saving;
    private String currentUserId;
    private User currentUser;
    private SavingComment repliedComment;

    private ScrollView scrollView;
    private View mainView;
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private TextView cityText;
    private TextView createdAtText;
    private SpannableTextView detailsText;
    private GridView gridGallery;
    private ImageView imageGallery;
    private ViewGroup likesLayout;
    private TextView commentText;
    private LikeTextView likeText;
    private LikesLayoutListener likesLayoutListener;

    private EditText editComment;
    private Button postCommentButton;
    private final TextWatcher watcher = validationTextWatcher();

    GalleryAdapter adapter;
    private SafeAsyncTask<Boolean> task;
    private ProgressListener progressListener;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            SavingCommentsFragment fragment = new SavingCommentsFragment();
            fragment.setSaving(saving);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        savingId = intent.getStringExtra(SavingDetailsActivity.INTENT_SAVING_ID);
        currentUserId = apiKeyProvider.getAuthUserId();
        likesLayoutListener = new LikesLayoutListener(getActivity(), currentUserId);
        shareService = new ShareService(getActivity());
        progressListener = (ProgressListener) getActivity();
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
                if (getActivity() != null && saving != null) {
                    showActionDialog(saving.isOwner(currentUserId));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saving_details, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        mainView = view.findViewById(R.id.main_view);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        cityText = (TextView) view.findViewById(R.id.text_city);
        createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        detailsText = (SpannableTextView) view.findViewById(R.id.text_details);
        adapter = new GalleryAdapter(getActivity(), R.layout.image_collection_listitem_inner);

        gridGallery = (GridView) view.findViewById(R.id.grid_gallery);
        gridGallery.setAdapter(adapter);
        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                clickImageByIndex(position);
            }
        });
        imageGallery = (ImageView) view.findViewById(R.id.image_gallery);
        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickImageByIndex(0);
            }
        });

        likesLayout = (ViewGroup) view.findViewById(R.id.layout_likes);

        commentText = (TextView) view.findViewById(R.id.text_comment);
        likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.setTextResId(R.string.label_like);
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up);

        editComment = (EditText) view.findViewById(R.id.edit_comment);
        editComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND)
                    submitComment();
                return false;
            }
        });
        editComment.addTextChangedListener(watcher);

        postCommentButton = (Button) view.findViewById(R.id.b_post_comment);
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitComment();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        updateUIWithValidation();
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        mainView = null;

        super.onDestroyView();
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(editComment);
        if (postCommentButton != null) {
            postCommentButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Override
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected Saving loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        saving = savingStore.getSavingByIdFromServer(savingId);
        if (saving.getDeletedAt() != null) {
            throw new Exception(getString(R.string.error_saving_was_deleted));
        }
        User user = userStore.getUserById(saving.getUserId());
        saving.setUser(user);
        return saving;
    }

    @Override
    protected void handleLoadResult(Saving result) {
        if (saving != null) {
            setData(saving);
            updateSaving(saving);
            handler.sendEmptyMessage(0);
        }
    }

    private void showActionDialog(final boolean isOwner) {
        List<String> options = new ArrayList<String>();
        options.add(getString(R.string.label_share));
        if (isOwner) {
            options.add(getString(R.string.label_delete));
        } else {
            options.add(getString(R.string.label_report));
        }
        new ListViewDialog(getActivity(), null, options,
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                showShare();
                                break;
                            case 1:
                                if (isOwner) {
                                    deleteSaving();
                                } else {
                                    reportSaving();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void showShare() {
        String imageUrl = null;
        if (saving.getImages().size() > 0) {
            imageUrl = saving.getImages().get(0);
        }
        shareService.show(saving.getDetails(), null, imageUrl);
    }

    private void deleteSaving() {
        if (task != null) {
            return;
        }
        progressListener.showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteSaving(savingId);
                savingStore.deleteSaving(savingId);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(getActivity(), cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                Toaster.showShort(getActivity(), R.string.info_saving_deleted);

                final Intent intent = new Intent();
                intent.putExtra(SavingDetailsActivity.INTENT_SAVING_ID, savingId);
                intent.putExtra(SavingDetailsActivity.INTENT_DELETED, true);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                progressListener.hideProgress();
                task = null;
            }
        };
        task.execute();
    }

    private void reportSaving() {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_SAVING);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, savingId);
        startActivity(intent);
    }

    private void updateSaving(Saving saving) {
        User user = saving.getUser();
        avatarImage.getFromUrl(user.getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), saving.getUserId()));

        userNameText.setText(user.getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), saving.getUserId()));
        cityText.setText(user.getCity());
        createdAtText.setText(saving.getCreatedAtFormatted());

        if (saving.getDetails() != null && saving.getDetails().length() > 0) {
            detailsText.setSpannableText(saving.getDetails());
        } else {
            detailsText.setVisibility(View.GONE);
        }

        ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
        for (String imageUrl: saving.getImages()) {
            CustomGallery item = new CustomGallery();
            item.type = CustomGallery.HTTP;
            item.imageUri = imageUrl;
            list.add(item);
        }
        gridGallery.setVisibility(View.GONE);
        imageGallery.setVisibility(View.GONE);
        if (list.size() > 0) {
            if (list.size() > 1) {
                adapter.addAll(list);
                gridGallery.setVisibility(View.VISIBLE);
            } else {
                ImageLoaderUtils.displayImage(list.get(0).getUri(), imageGallery);
                imageGallery.setVisibility(View.VISIBLE);
            }
        }

        int comments = saving.getCommentsCount();
        commentText.setText(comments > 0 ? String.valueOf(comments) : getString(R.string.label_comment));
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        likeText.init(saving.getLikesCount(), saving.isLiked(currentUserId));
        SavingLikeListener likeListener = new SavingLikeListener(saving);
        likeListener.setOnLikeFinishedListener(new SavingLikeListener.LikeFinishedListener() {
            @Override
            public void OnLikeFinished(Saving saving) {
                likesLayoutListener.update(likesLayout, saving.getLikes());
            }

            @Override
            public void OnUnLikeFinished(Saving saving) {
                likesLayoutListener.update(likesLayout, saving.getLikes());
            }
        });
        likeText.setLikeListener(likeListener);
        likesLayoutListener.update(likesLayout, saving.getLikes());
    }

    private void disableSubmit() {
        postCommentButton.setEnabled(false);
    }

    private void resetCommentBox() {
        EditTextUtils.hideSoftInput(editComment);
        editComment.clearFocus();
        editComment.setText(null);
        editComment.setHint(R.string.hint_new_saving_comment);
    }

    private void submitComment() {
        String repliedId = null;
        String content = editComment.getText().toString();
        if (repliedComment != null) {
            repliedId = repliedComment.getObjectId();
            content = getString(R.string.hint_reply_saving_comment,
                    repliedComment.getUser().getScreenName(), content);
        }

        bus.post(new AddSavingCommentEvent(repliedId, content));

        disableSubmit();
        resetCommentBox();
    }

    @Subscribe
    public void onReplySavingCommentEvent(final ReplySavingCommentEvent event) {
        EditTextUtils.showSoftInput(editComment, true);
        repliedComment = event.getComment();
        editComment.setHint(getString(R.string.hint_reply_saving_comment,
                repliedComment.getUser().getScreenName(), repliedComment.getContent()));
    }

    @Subscribe
    public void onAddSavingCommentFinishedEvent(final AddSavingCommentFinishedEvent event) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                sendCommentMessage(event.getContent());
                if (repliedComment != null) {
                    sendRepliedMessage(repliedComment);
                }
                return true;
            }
        }.execute();
    }

    private void sendMessage(String to, CmdMessage cmdMessage) {
        if (!to.equals(currentUser.getChatId())) {
            chatService.sendCmdMessage(to, cmdMessage, false, null);
        }
    }

    private void sendSavingOwnerMessage(CmdMessage cmdMessage) throws Exception {
        User savingOwner = userStore.getUserById(saving.getUserId());
        sendMessage(savingOwner.getChatId(), cmdMessage);
    }

    private void sendCommentMessage(String content) throws Exception {
        String title = getString(R.string.label_comment_saving_message,
                currentUser.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.SAVING_COMMENT,
                title, content, savingId);
        sendSavingOwnerMessage(cmdMessage);
    }

    private void sendRepliedMessage(SavingComment repliedComment) throws Exception {
        String title = getString(R.string.label_replied_saving_message,
                currentUser.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.SAVING_REPLY,
                title, repliedComment.getContent(), savingId);
        sendSavingOwnerMessage(cmdMessage);
        if (!saving.isOwner(repliedComment.getUserId())) {
            sendMessage(repliedComment.getUser().getChatId(), cmdMessage);
        }
    }

    private void clickImageByIndex(int index) {
        String imageUrl = saving.getImages().get(index);
        final Intent intent = new Intent(getActivity(), ImageViewActivity.class);
        intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
        startActivity(intent);
    }
}
