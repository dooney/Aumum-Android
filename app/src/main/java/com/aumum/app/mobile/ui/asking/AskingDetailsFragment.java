package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.aumum.app.mobile.events.AddAskingReplyEvent;
import com.aumum.app.mobile.events.AddAskingReplyFinishedEvent;
import com.aumum.app.mobile.events.ReplyAskingReplyEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
import com.aumum.app.mobile.ui.view.QuickReturnScrollView;
import com.aumum.app.mobile.ui.view.SpannableTextView;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.Ln;
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
public class AskingDetailsFragment extends LoaderFragment<Asking>
        implements QuickReturnScrollView.OnScrollDirectionListener {

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject AskingStore askingStore;
    @Inject Bus bus;
    @Inject ApiKeyProvider apiKeyProvider;

    private Asking asking;
    private String askingId;
    private String currentUserId;

    private QuickReturnScrollView scrollView;
    private View mainView;
    private SpannableTextView questionText;
    private TextView userNameText;
    private TextView areaText;
    private TextView updatedAtText;
    private FavoriteTextView favoriteText;

    private ViewGroup layoutAction;
    private ViewGroup layoutReplyBox;
    private TextView replyText;
    private boolean isReplyBoxShow;
    private EditText editReply;
    private ImageView postReplyButton;

    private SafeAsyncTask<Boolean> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        askingId = intent.getStringExtra(AskingDetailsActivity.INTENT_ASKING_ID);
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

        scrollView = (QuickReturnScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOnScrollDirectionListener(this);

        mainView = view.findViewById(R.id.main_view);
        questionText = (SpannableTextView) view.findViewById(R.id.text_question);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        areaText = (TextView) view.findViewById(R.id.text_area);
        updatedAtText = (TextView) view.findViewById(R.id.text_updatedAt);

        favoriteText = (FavoriteTextView) view.findViewById(R.id.text_favorite);
        favoriteText.setFavoriteResId(R.drawable.ic_fa_star_o_s);
        favoriteText.setFavoritedResId(R.drawable.ic_fa_star_s);

        layoutAction = (ViewGroup) view.findViewById(R.id.layout_action);
        layoutReplyBox = (ViewGroup) view.findViewById(R.id.layout_reply_box);
        replyText = (TextView) view.findViewById(R.id.text_reply);
        replyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleReplyBox();
            }
        });
        editReply = (EditText) view.findViewById(R.id.edit_reply);
        postReplyButton = (ImageView) view.findViewById(R.id.image_post_reply);
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

            userNameText.setText(asking.getUser().getScreenName());
            userNameText.setOnClickListener(new UserListener(getActivity(), asking.getUserId()));
            areaText.setText(Constants.Options.AREA_OPTIONS[asking.getUser().getArea()]);
            questionText.setSpannableText(asking.getQuestion());
            updatedAtText.setText(asking.getUpdatedAtFormatted());
            favoriteText.init(asking.getFavoritesCount(), asking.isFavorited(currentUserId));
            favoriteText.setFavoriteListener(new AskingFavoriteListener(asking));
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onScrollUp() {
        if (!isReplyBoxShow) {
            Animation.animateIconBar(layoutAction, true);
        }
    }

    @Override
    public void onScrollDown() {
        if (isReplyBoxShow) {
            return;
        }

        boolean canScrollDown = scrollView.canScrollDown();
        boolean canScrollUp = scrollView.canScrollUp();
        if (!canScrollDown) {
            Animation.animateIconBar(layoutAction, true);
        } else if (canScrollDown && canScrollUp) {
            Animation.animateIconBar(layoutAction, false);
        }
    }

    private void toggleReplyBox() {
        if (isReplyBoxShow) {
            hideReplyBox();
        } else {
            editReply.setHint(R.string.hint_new_reply);
            showReplyBox();
        }
        isReplyBoxShow = !isReplyBoxShow;
    }

    private void showReplyBox() {
        Animation.flyIn(layoutReplyBox);
        EditTextUtils.showSoftInput(editReply, true);
    }

    private void hideReplyBox() {
        EditTextUtils.hideSoftInput(editReply);
        editReply.setText(null);
        Animation.flyOut(layoutReplyBox);
    }

    private void enableSubmit() {
        postReplyButton.setEnabled(true);
    }

    private void disableSubmit() {
        postReplyButton.setEnabled(false);
    }

    private void submitReply() {
        String answer = editReply.getText().toString();
        bus.post(new AddAskingReplyEvent(answer));

        hideReplyBox();
        disableSubmit();
    }

    @Subscribe
    public void onReplyAskingReplyEvent(ReplyAskingReplyEvent event) {
        showReplyBox();
        editReply.setHint(event.getReplyHint());
    }

    @Subscribe
    public void onAddAskingReplyFinishedEvent(AddAskingReplyFinishedEvent event) {
        enableSubmit();
    }

    private void showActionDialog(boolean isOwner) {
        List<String> options = new ArrayList<String>();
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
                                break;
                            case 1:
                                deleteAsking();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void deleteAsking() {
        showProgress();

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteAsking(askingId);
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
