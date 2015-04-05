package com.aumum.app.mobile.ui.moment;

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
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.service.ShareService;
import com.aumum.app.mobile.events.AddMomentCommentEvent;
import com.aumum.app.mobile.events.AddMomentCommentFinishedEvent;
import com.aumum.app.mobile.events.ReplyMomentCommentEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.base.ProgressListener;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.like.LikesLayoutListener;
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
 * Created by Administrator on 3/03/2015.
 */
public class MomentDetailsFragment extends LoaderFragment<Moment> {

    @Inject Bus bus;
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject RestService restService;
    @Inject ChatService chatService;
    private ShareService shareService;

    private String momentId;
    private Moment moment;
    private String currentUserId;
    private User currentUser;
    private MomentComment repliedComment;

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
            MomentCommentsFragment fragment = new MomentCommentsFragment();
            fragment.setMoment(moment);
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
        momentId = intent.getStringExtra(MomentDetailsActivity.INTENT_MOMENT_ID);
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
                if (getActivity() != null && moment != null) {
                    showActionDialog(moment.isOwner(currentUserId));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moment_details, null);
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
    protected Moment loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        moment = momentStore.getMomentByIdFromServer(momentId);
        if (moment.getDeletedAt() != null) {
            throw new Exception(getString(R.string.error_moment_was_deleted));
        }
        User user = userStore.getUserById(moment.getUserId());
        moment.setUser(user);
        return moment;
    }

    @Override
    protected void handleLoadResult(Moment result) {
        if (moment != null) {
            setData(moment);
            updateMoment(moment);
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
                                    deleteMoment();
                                } else {
                                    reportMoment();
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
        if (moment.getImages().size() > 0) {
            imageUrl = moment.getImages().get(0);
        }
        shareService.show(moment.getDetails(), null, imageUrl);
    }

    private void deleteMoment() {
        if (task != null) {
            return;
        }
        progressListener.showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteMoment(momentId);
                momentStore.deleteMoment(momentId);
                updateCredit(currentUser, CreditRule.DELETE_MOMENT);
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
                Toaster.showShort(getActivity(), R.string.info_moment_deleted);

                final Intent intent = new Intent();
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID, momentId);
                intent.putExtra(MomentDetailsActivity.INTENT_DELETED, true);
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

    private void reportMoment() {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_MOMENT);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, momentId);
        startActivity(intent);
    }

    private void updateMoment(Moment moment) {
        User user = moment.getUser();
        avatarImage.getFromUrl(user.getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), moment.getUserId()));

        userNameText.setText(user.getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), moment.getUserId()));
        cityText.setText(user.getCity());
        createdAtText.setText(moment.getCreatedAtFormatted());

        if (moment.getDetails() != null && moment.getDetails().length() > 0) {
            detailsText.setSpannableText(moment.getDetails());
        } else {
            detailsText.setVisibility(View.GONE);
        }

        ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
        for (String imageUrl: moment.getImages()) {
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

        int comments = moment.getCommentsCount();
        commentText.setText(comments > 0 ? String.valueOf(comments) : getString(R.string.label_comment));
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        likeText.init(moment.getLikesCount(), moment.isLiked(currentUserId));
        MomentLikeListener likeListener = new MomentLikeListener(moment);
        likeListener.setOnLikeFinishedListener(new MomentLikeListener.LikeFinishedListener() {
            @Override
            public void OnLikeFinished(Moment moment) {
                likesLayoutListener.update(likesLayout, moment.getLikes());
            }

            @Override
            public void OnUnLikeFinished(Moment moment) {
                likesLayoutListener.update(likesLayout, moment.getLikes());
            }
        });
        likeText.setLikeListener(likeListener);
        likesLayoutListener.update(likesLayout, moment.getLikes());
    }

    private void disableSubmit() {
        postCommentButton.setEnabled(false);
    }

    private void resetCommentBox() {
        EditTextUtils.hideSoftInput(editComment);
        editComment.clearFocus();
        editComment.setText(null);
        editComment.setHint(R.string.hint_new_moment_comment);
    }

    private void submitComment() {
        String repliedId = null;
        String content = editComment.getText().toString();
        if (repliedComment != null) {
            repliedId = repliedComment.getObjectId();
            content = getString(R.string.hint_reply_moment_comment,
                    repliedComment.getUser().getScreenName(), content);
        }

        bus.post(new AddMomentCommentEvent(repliedId, content));

        disableSubmit();
        resetCommentBox();
    }

    @Subscribe
    public void onReplyMomentCommentEvent(final ReplyMomentCommentEvent event) {
        EditTextUtils.showSoftInput(editComment, true);
        repliedComment = event.getComment();
        editComment.setHint(getString(R.string.hint_reply_moment_comment,
                repliedComment.getUser().getScreenName(), repliedComment.getContent()));
    }

    @Subscribe
    public void onAddMomentCommentFinishedEvent(final AddMomentCommentFinishedEvent event) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (!moment.isOwner(currentUserId)) {
                    sendCommentMessage(event.getContent());
                    updateCredit(currentUser, CreditRule.ADD_MOMENT_COMMENT);
                }
                if (repliedComment != null &&
                    !moment.isOwner(repliedComment.getUserId()) &&
                    !repliedComment.isOwner(currentUserId)) {
                    sendRepliedMessage(repliedComment);
                }
                return true;
            }
        }.execute();
    }

    private void sendCommentMessage(String content) throws Exception {
        String title = getString(R.string.label_comment_moment_message,
                currentUser.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.MOMENT_COMMENT,
                title, content, momentId);
        User momentOwner = userStore.getUserById(moment.getUserId());
        chatService.sendCmdMessage(momentOwner.getChatId(), cmdMessage, false, null);
    }

    private void sendRepliedMessage(MomentComment replied) throws Exception {
        String title = getString(R.string.label_replied_moment_message,
                currentUser.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.MOMENT_REPLY,
                title, replied.getContent(), momentId);
        chatService.sendCmdMessage(replied.getUser().getChatId(), cmdMessage, false, null);
    }

    private void clickImageByIndex(int index) {
        String imageUrl = moment.getImages().get(index);
        final Intent intent = new Intent(getActivity(), ImageViewActivity.class);
        intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
        startActivity(intent);
    }

    private void updateCredit(User currentUser, int seq) throws Exception {
        final CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            final int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
            userStore.save(currentUser);
            if (credit > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.showShort(getActivity(), getString(R.string.info_got_credit,
                                creditRule.getDescription(), credit));
                    }
                });
            }
        }
    }
}
