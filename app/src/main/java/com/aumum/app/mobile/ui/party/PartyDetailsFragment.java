package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.AddPartyReasonEvent;
import com.aumum.app.mobile.events.AddPartyReasonFinishedEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.QuickReturnScrollView;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.GPSTracker;
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
 * Created by Administrator on 16/10/2014.
 */
public class PartyDetailsFragment extends LoaderFragment<Party>
        implements QuickReturnScrollView.OnScrollDirectionListener {
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject Bus bus;
    @Inject UserStore userStore;
    @Inject PartyStore partyStore;
    @Inject RestService restService;
    @Inject MessageDeliveryService messageDeliveryService;

    private Party party;
    private String partyId;
    private String currentUserId;

    private GPSTracker gpsTracker;

    private QuickReturnScrollView scrollView;
    private View mainView;
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private TextView titleText;
    private TextView distanceText;
    private TextView areaText;
    private TextView createdAtText;
    private TextView timeText;
    private TextView locationText;
    private TextView ageText;
    private TextView genderText;
    private TextView detailsText;
    private ViewGroup membersLayout;
    private ViewGroup likesLayout;

    private ViewGroup actionLayout;
    private boolean showAction;
    private ViewGroup joinBoxLayout;
    private JoinTextView joinText;
    private TextView commentText;
    private LikeTextView likeText;
    private FavoriteTextView favoriteText;
    private EditText editReason;
    private ImageView postReasonButton;
    private boolean isJoinBoxShow;

    private MembersLayoutListener membersLayoutListener;
    private LikesLayoutListener likesLayoutListener;

    private SafeAsyncTask<Boolean> task;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        partyId = intent.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
        currentUserId = apiKeyProvider.getAuthUserId();
        membersLayoutListener = new MembersLayoutListener(getActivity(), currentUserId);
        likesLayoutListener = new LikesLayoutListener(getActivity(), currentUserId);
        gpsTracker = new GPSTracker(getActivity());
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable() || party == null) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog(party.isOwner(currentUserId));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_details, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollView = (QuickReturnScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOnScrollDirectionListener(this);

        mainView = view.findViewById(R.id.main_view);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        titleText = (TextView) view.findViewById(R.id.text_title);
        distanceText = (TextView) view.findViewById(R.id.text_distance);
        areaText = (TextView) view.findViewById(R.id.text_area);
        createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        timeText = (TextView) view.findViewById(R.id.text_time);
        locationText = (TextView) view.findViewById(R.id.text_location);
        ageText = (TextView) view.findViewById(R.id.text_age);
        genderText = (TextView) view.findViewById(R.id.text_gender);
        detailsText = (TextView) view.findViewById(R.id.text_details);

        membersLayout = (ViewGroup) view.findViewById(R.id.layout_members);
        likesLayout = (ViewGroup) view.findViewById(R.id.layout_likes);

        actionLayout = (ViewGroup) view.findViewById(R.id.layout_action);
        joinBoxLayout = (ViewGroup) view.findViewById(R.id.layout_join_box);
        joinText = (JoinTextView) view.findViewById(R.id.text_join);
        joinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleJoinBox();
            }
        });
        commentText = (TextView) view.findViewById(R.id.text_comment);

        likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.setTextResId(R.string.label_like);
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up);

        favoriteText = (FavoriteTextView) view.findViewById(R.id.text_favorite);
        favoriteText.setFavoriteResId(R.drawable.ic_fa_star_o);
        favoriteText.setFavoritedResId(R.drawable.ic_fa_star);

        editReason = (EditText) view.findViewById(R.id.edit_reason);
        postReasonButton = (ImageView) view.findViewById(R.id.image_post_reason);
        postReasonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
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
        return R.string.error_load_party_details;
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
    protected Party loadDataCore(Bundle bundle) throws Exception {
        party = partyStore.getPartyByIdFromServer(partyId);
        party.setDistance(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        return party;
    }

    @Override
    protected void handleLoadResult(final Party party) {
        try {
            if (party != null) {
                setData(party);

                User user = userStore.getUserById(party.getUserId());
                avatarImage.getFromUrl(user.getAvatarUrl());
                avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));

                userNameText.setText(user.getScreenName());
                userNameText.setOnClickListener(new UserListener(userNameText.getContext(), party.getUserId()));
                titleText.setText(party.getTitle());
                distanceText.setText(getString(R.string.label_distance, party.getDistance()));
                areaText.setText(Constants.Options.AREA_OPTIONS[user.getArea()]);
                createdAtText.setText(party.getCreatedAtFormatted());
                timeText.setText(party.getDateTimeText());
                locationText.setText(party.getPlace().getLocation());
                ageText.setText(Constants.Options.AGE_OPTIONS[party.getAge()]);
                genderText.setText(Constants.Options.GENDER_OPTIONS[party.getGender()]);
                detailsText.setText(party.getDetails());

                showAction = false;
                if (!party.isExpired() && !party.isOwner(currentUserId)) {
                    showAction = true;
                    actionLayout.setVisibility(View.VISIBLE);
                    joinText.update(party.isMember(currentUserId));
                }

                int comments = party.getCommentsCount();
                commentText.setText(comments > 0 ? String.valueOf(comments) : getString(R.string.label_comment));
                commentText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation.animateTextView(view);
                        final Intent intent = new Intent(getActivity(), PartyCommentsActivity.class);
                        intent.putExtra(PartyCommentsActivity.INTENT_PARTY_ID, party.getObjectId());
                        getActivity().startActivity(intent);
                    }
                });

                likeText.init(party.getLikesCount(), party.isLiked(currentUserId));
                PartyLikeListener likeListener = new PartyLikeListener(party);
                likeListener.setOnLikeFinishedListener(new PartyLikeListener.LikeFinishedListener() {
                    @Override
                    public void OnLikeFinished(Party party) {
                        updateLikesLayout(party.getLikes());
                    }

                    @Override
                    public void OnUnLikeFinished(Party party) {
                        updateLikesLayout(party.getLikes());
                    }
                });
                likeText.setLikeListener(likeListener);

                favoriteText.init(party.getFavoritesCount(), party.isFavorited(currentUserId));
                favoriteText.setFavoriteListener(new PartyFavoriteListener(party));

                membersLayoutListener.update(membersLayout, party.getMembers());

                updateLikesLayout(party.getLikes());
            }
        } catch (Exception e) {
            Ln.d(e);
        }
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
                                deleteParty();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void deleteParty() {
        showProgress();

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteParty(party.getObjectId());
                for(String userId: party.getMembers()) {
                    restService.removeUserParty(userId, party.getObjectId());

                    String content = getActivity().getString(R.string.label_delete_party_message, party.getTitle());
                    Message message = new Message(Message.Type.PARTY_DELETE,
                            party.getUserId(), userId, content, party.getObjectId());
                    messageDeliveryService.send(message);
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                    Toaster.showShort(getActivity(), R.string.error_delete_party);
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                Toaster.showShort(getActivity(), R.string.info_party_deleted);

                final Intent intent = new Intent();
                intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, partyId);
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

    private void updateLikesLayout(List<String> likes) {
        try {
            likesLayoutListener.update(likesLayout, likes);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    private void hideJoinBox() {
        EditTextUtils.hideSoftInput(editReason);
        editReason.setText(null);
        Animation.flyOut(joinBoxLayout);
    }

    private void showJoinBox() {
        Animation.flyIn(joinBoxLayout);
        EditTextUtils.showSoftInput(editReason, true);
    }

    private void toggleJoinBox() {
        if (isJoinBoxShow) {
            hideJoinBox();
        } else {
            if (joinText.isMember()) {
                editReason.setHint(R.string.hint_quit_reason);
            } else {
                editReason.setHint(R.string.hint_join_reason);
            }
            showJoinBox();
        }
        isJoinBoxShow = !isJoinBoxShow;
    }

    private void enableSubmit() {
        postReasonButton.setEnabled(true);
    }

    private void disableSubmit() {
        postReasonButton.setEnabled(false);
    }

    private void submit() {
        String reason = editReason.getText().toString();
        if (joinText.isMember()) {
            bus.post(new AddPartyReasonEvent(PartyReason.QUIT, reason));
        } else {
            bus.post(new AddPartyReasonEvent(PartyReason.JOIN, reason));
        }

        hideJoinBox();
        disableSubmit();
    }

    @Subscribe
    public void onAddPartyReasonFinishedEvent(AddPartyReasonFinishedEvent event) {
        if (event.getType() == PartyReason.JOIN) {
            joinText.update(true);
        } else if (event.getType() == PartyReason.QUIT) {
            joinText.update(false);
        }

        try {
            membersLayoutListener.update(membersLayout, event.getParty().getMembers());
        } catch (Exception e) {
            Ln.e(e);
        }
        
        enableSubmit();
    }

    @Override
    public void onScrollUp() {
        if (!isJoinBoxShow && showAction) {
            Animation.animateIconBar(actionLayout, true);
        }
    }

    @Override
    public void onScrollDown() {
        if (isJoinBoxShow || !showAction) {
            return;
        }

        boolean canScrollDown = scrollView.canScrollDown();
        boolean canScrollUp = scrollView.canScrollUp();
        if (!canScrollDown) {
            Animation.animateIconBar(actionLayout, true);
        } else if (canScrollDown && canScrollUp) {
            Animation.animateIconBar(actionLayout, false);
        }
    }
}
