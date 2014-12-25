package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.service.ShareService;
import com.aumum.app.mobile.events.AddAskingReplyEvent;
import com.aumum.app.mobile.events.ReplyAskingReplyEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.UpYunUtils;
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
    @Inject Bus bus;
    @Inject ApiKeyProvider apiKeyProvider;
    private ShareService shareService;

    private Asking asking;
    private String askingId;
    private String currentUserId;

    private View mainView;
    private SpannableTextView titleText;
    private SpannableTextView detailsText;
    private GridView gridGallery;
    private TextView userNameText;
    private TextView cityText;
    private TextView updatedAtText;
    private FavoriteTextView favoriteText;

    private EditText editReply;
    private Button postReplyButton;

    GalleryAdapter adapter;
    private SafeAsyncTask<Boolean> task;
    private final TextWatcher watcher = validationTextWatcher();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        askingId = intent.getStringExtra(AskingDetailsActivity.INTENT_ASKING_ID);

        shareService = new ShareService(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable() || asking == null) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog(asking.isOwner(currentUserId));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking_details, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view.findViewById(R.id.main_view);
        titleText = (SpannableTextView) view.findViewById(R.id.text_title);
        detailsText = (SpannableTextView) view.findViewById(R.id.text_details);

        adapter = new GalleryAdapter(getActivity(), R.layout.image_collection_listitem_inner, ImageLoaderUtils.getInstance());
        gridGallery = (GridView) view.findViewById(R.id.grid_gallery);
        gridGallery.setAdapter(adapter);

        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        cityText = (TextView) view.findViewById(R.id.text_city);
        updatedAtText = (TextView) view.findViewById(R.id.text_updatedAt);

        favoriteText = (FavoriteTextView) view.findViewById(R.id.text_favorite);
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
    public void onDestroyView() {
        mainView = null;

        super.onDestroyView();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_asking;
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
        User user = userStore.getUserById(asking.getUserId());
        asking.setUser(user);
        return asking;
    }

    @Override
    protected void handleLoadResult(Asking asking) {
        try {
            setData(asking);

            ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
            for (String imageUrl: asking.getImages()) {
                CustomGallery item = new CustomGallery();
                item.type = CustomGallery.HTTP;
                item.imageUri = UpYunUtils.getThumbnailUrl(imageUrl);
                list.add(item);
            }
            if (list.size() > 0) {
                adapter.addAll(list);
            } else {
                gridGallery.setVisibility(View.GONE);
            }

            userNameText.setText(asking.getUser().getScreenName());
            userNameText.setOnClickListener(new UserListener(getActivity(), asking.getUserId()));
            cityText.setText(Constants.Options.CITY_OPTIONS[asking.getUser().getCity()]);
            titleText.setSpannableText(asking.getTitle());
            if (asking.getDetails() != null && asking.getDetails().length() > 0) {
                detailsText.setSpannableText(asking.getDetails());
            } else {
                detailsText.setVisibility(View.GONE);
            }
            updatedAtText.setText(asking.getUpdatedAtFormatted());
            favoriteText.init(asking.getFavoritesCount(), asking.isFavorited(currentUserId));
            favoriteText.setFavoriteListener(new AskingFavoriteListener(asking));
        } catch (Exception e) {
            Ln.e(e);
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
        final boolean populated = populated(editReply);
        postReplyButton.setEnabled(populated);
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

    private void showActionDialog(boolean isOwner) {
        List<String> options = new ArrayList<String>();
        options.add(getString(R.string.label_share));
        options.add(getString(R.string.label_report));
        if (isOwner) {
            options.add(getString(R.string.label_delete));
        }
        DialogUtils.showDialog(getActivity(), options.toArray(new CharSequence[options.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                showShare();
                                break;
                            case 1:
                                break;
                            case 2:
                                deleteAsking();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void showShare() {
        shareService.show(getActivity());
    }

    private void deleteAsking() {
        if (task != null) {
            return;
        }
        showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteAsking(askingId);
                askingStore.deleteAsking(askingId);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                    Toaster.showShort(getActivity(), R.string.error_delete_asking);
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
                hideProgress();
                task = null;
            }
        };
        task.execute();
    }
}
