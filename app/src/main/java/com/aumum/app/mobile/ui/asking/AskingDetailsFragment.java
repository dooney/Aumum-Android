package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.service.ShareService;
import com.aumum.app.mobile.events.AddAskingReplyEvent;
import com.aumum.app.mobile.events.ReplyAskingReplyEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.base.ProgressListener;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
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
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingDetailsFragment extends LoaderFragment<Asking> {

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject AskingStore askingStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject Bus bus;
    @Inject ApiKeyProvider apiKeyProvider;
    private ShareService shareService;

    private Asking asking;
    private String askingId;
    private String currentUserId;

    private ScrollView scrollView;
    private View mainView;
    private SpannableTextView titleText;
    private SpannableTextView detailsText;
    private GridView gridGallery;
    private ImageView imageGallery;
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private TextView updatedAtText;
    private TextView replyText;
    private LikeTextView likeText;
    private FavoriteTextView favoriteText;

    private EditText editReply;
    private Button postReplyButton;

    GalleryAdapter adapter;
    private SafeAsyncTask<Boolean> task;
    private final TextWatcher watcher = validationTextWatcher();
    private ProgressListener progressListener;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            AskingRepliesFragment fragment = new AskingRepliesFragment();
            fragment.setAsking(asking);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        askingId = intent.getStringExtra(AskingDetailsActivity.INTENT_ASKING_ID);

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
                if (getActivity() != null && asking != null) {
                    showActionDialog(asking.isOwner(currentUserId));
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking_details, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);

        mainView = view.findViewById(R.id.main_view);
        titleText = (SpannableTextView) view.findViewById(R.id.text_title);
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

        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        updatedAtText = (TextView) view.findViewById(R.id.text_updatedAt);

        replyText = (TextView) view.findViewById(R.id.text_reply);

        likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.setTextResId(R.string.label_like);
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up_s);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up_s);

        favoriteText = (FavoriteTextView) view.findViewById(R.id.text_favorite);
        favoriteText.setTextResId(R.string.label_favorite);
        favoriteText.setFavoriteResId(R.drawable.ic_fa_star_o_s);
        favoriteText.setFavoritedResId(R.drawable.ic_fa_star_s);

        editReply = (EditText) view.findViewById(R.id.edit_reply);
        editReply.addTextChangedListener(watcher);
        postReplyButton = (Button) view.findViewById(R.id.b_post_reply);
        postReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReply();
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
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected Asking loadDataCore(Bundle bundle) throws Exception {
        currentUserId = apiKeyProvider.getAuthUserId();
        asking = askingStore.getAskingByIdFromServer(askingId);
        if (asking.getDeletedAt() != null) {
            throw new Exception(getString(R.string.error_asking_was_deleted));
        }
        User user = userStore.getUserById(asking.getUserId());
        asking.setUser(user);
        return asking;
    }

    @Override
    protected void handleLoadResult(Asking asking) {
        setData(asking);
        updateAsking(asking);
        handler.sendEmptyMessage(0);
    }

    private void updateAsking(Asking asking) {
        ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
        for (String imageUrl: asking.getImages()) {
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

        if (asking.getIsAnonymous()) {
            avatarImage.setImageResource(R.drawable.ic_avatar);
            userNameText.setText(getString(R.string.label_post_owner));
        } else {
            avatarImage.getFromUrl(asking.getUser().getAvatarUrl());
            userNameText.setText(asking.getUser().getScreenName());
            userNameText.setOnClickListener(new UserListener(getActivity(), asking.getUserId()));
        }
        titleText.setSpannableText(asking.getTitle());
        if (asking.getDetails() != null && asking.getDetails().length() > 0) {
            detailsText.setSpannableText(asking.getDetails());
        } else {
            detailsText.setVisibility(View.GONE);
        }
        updatedAtText.setText(asking.getUpdatedAtFormatted());
        replyText.setText(String.valueOf(asking.getRepliesCount()));
        replyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditTextUtils.showSoftInput(editReply, true);
            }
        });
        likeText.init(asking.getLikesCount(), asking.isLiked(currentUserId));
        likeText.setLikeListener(new AskingLikeListener(asking));
        favoriteText.init(asking.getFavoritesCount(), asking.isFavorited(currentUserId));
        favoriteText.setFavoriteListener(new AskingFavoriteListener(asking));
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(editReply);
        if (postReplyButton != null) {
            postReplyButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void disableSubmit() {
        postReplyButton.setEnabled(false);
    }

    private void resetReplyBox() {
        EditTextUtils.hideSoftInput(editReply);
        editReply.clearFocus();
        editReply.setText(null);
        editReply.setHint(R.string.hint_new_reply);
    }

    private void submitReply() {
        String answer = editReply.getText().toString();
        bus.post(new AddAskingReplyEvent(answer));

        disableSubmit();
        resetReplyBox();
    }

    @Subscribe
    public void onReplyAskingReplyEvent(ReplyAskingReplyEvent event) {
        EditTextUtils.showSoftInput(editReply, true);
        editReply.setHint(event.getReplyHint());
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
                            deleteAsking();
                        } else {
                            reportAsking();
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
        if (asking.getImages().size() > 0) {
            imageUrl = asking.getImages().get(0);
        }
        shareService.show(asking.getTitle(), asking.getDetails(), imageUrl);
    }

    private void deleteAsking() {
        if (task != null) {
            return;
        }
        progressListener.showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteAsking(askingId);
                askingStore.deleteAsking(askingId);
                User currentUser = userStore.getCurrentUser();
                updateCredit(currentUser, CreditRule.DELETE_ASKING);
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
                Toaster.showShort(getActivity(), R.string.info_asking_deleted);

                final Intent intent = new Intent();
                intent.putExtra(AskingDetailsActivity.INTENT_ASKING_ID, askingId);
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

    private void reportAsking() {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_ASKING);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, askingId);
        startActivity(intent);
    }

    private void clickImageByIndex(int index) {
        String imageUrl = asking.getImages().get(index);
        final Intent intent = new Intent(getActivity(), ImageViewActivity.class);
        intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, imageUrl);
        startActivity(intent);
    }

    private void updateCredit(User currentUser, int seq) throws Exception {
        CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
            userStore.save(currentUser);
        }
    }
}
