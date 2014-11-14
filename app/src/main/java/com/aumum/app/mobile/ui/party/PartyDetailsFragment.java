package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyReason;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.events.AddPartyReasonEvent;
import com.aumum.app.mobile.events.AddPartyReasonFinishedEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.DropdownImageView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.QuickReturnScrollView;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.GPSTracker;
import com.aumum.app.mobile.utils.Ln;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 16/10/2014.
 */
public class PartyDetailsFragment extends LoaderFragment<Party>
        implements PartyActionListener.OnActionListener,
                   PartyActionListener.OnProgressListener,
                   QuickReturnScrollView.OnScrollDirectionListener {
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject Bus bus;
    @Inject UserStore userStore;
    @Inject PartyStore partyStore;

    private String partyId;
    private String currentUserId;

    private GPSTracker gpsTracker;

    private QuickReturnScrollView scrollView;
    private View mainView;
    private DropdownImageView dropdownImage;
    private ProgressBar progressBar;
    private AvatarImageView avatarImage;
    private TextView userNameText;
    private TextView titleText;
    private TextView distanceText;
    private TextView createdAtText;
    private TextView dateText;
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
    private TextView checkInText;
    private ViewGroup checkInLayout;
    private TextView commentText;
    private LikeTextView likeText;
    private EditText editReason;
    private ImageView postReasonButton;
    private boolean isJoinBoxShow;

    private MembersLayoutListener membersLayoutListener;
    private LikesLayoutListener likesLayoutListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.invalid_party);
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
        dropdownImage = (DropdownImageView) view.findViewById(R.id.image_dropdown);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        titleText = (TextView) view.findViewById(R.id.text_title);
        distanceText = (TextView) view.findViewById(R.id.text_distance);
        createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        dateText = (TextView) view.findViewById(R.id.text_date);
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
        checkInText = (TextView) view.findViewById(R.id.text_check_in);
        checkInLayout = (ViewGroup) view.findViewById(R.id.layout_check_in);
        commentText = (TextView) view.findViewById(R.id.text_comment);
        likeText = (LikeTextView) view.findViewById(R.id.text_like);
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
        return R.string.error_load_party;
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
        Party party = partyStore.getPartyByIdFromServer(partyId);
        if (party == null) {
            throw new Exception(getString(R.string.invalid_party));
        }
        party.setDistance(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        return party;
    }

    @Override
    protected void handleLoadResult(final Party party) {
        try {
            if (party != null) {
                setData(party);

                User user;
                if (party.isOwner(currentUserId)) {
                    user = userStore.getCurrentUser();
                } else {
                    user = userStore.getUserById(party.getUserId());
                }
                avatarImage.getFromUrl(user.getAvatarUrl());
                avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));

                if (party.isOwner(currentUserId)) {
                    PartyOwnerActionListener listener = new PartyOwnerActionListener(getActivity(), party);
                    listener.setOnActionListener(this);
                    listener.setOnProgressListener(this);
                    dropdownImage.init(listener);
                } else {
                    PartyUserActionListener listener = new PartyUserActionListener(getActivity(), party);
                    listener.setOnActionListener(this);
                    listener.setOnProgressListener(this);
                    dropdownImage.init(listener);
                }

                userNameText.setText(user.getScreenName());
                userNameText.setOnClickListener(new UserListener(userNameText.getContext(), party.getUserId()));
                titleText.setText(party.getTitle());
                distanceText.setText(getString(R.string.label_distance, party.getDistance()));
                createdAtText.setText(party.getCreatedAtFormatted());
                dateText.setText(party.getDate().getDateText());
                timeText.setText(party.getTime().getTimeText());
                locationText.setText(party.getPlace().getLocation());
                ageText.setText(Constants.Options.AGE_OPTIONS[party.getAge()]);
                genderText.setText(Constants.Options.GENDER_OPTIONS[party.getGender()]);
                detailsText.setText(party.getDetails());

                showAction = false;
                if (party.isExpired() && party.isMember(currentUserId)) {
                    checkInLayout.setVisibility(View.VISIBLE);
                    checkInText.setText(party.getMomentCounts() > 0 ? String.valueOf(party.getMomentCounts()):
                            getString(R.string.label_check_in));
                    checkInText.setOnClickListener(new CheckInListener(getActivity(), party));
                }
                if (!party.isExpired() && !party.isOwner(currentUserId)) {
                    showAction = true;
                    actionLayout.setVisibility(View.VISIBLE);
                    joinText.update(party.isMember(currentUserId));
                }

                int comments = party.getCommentCounts();
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

                boolean isLike = party.isLike(currentUserId);
                likeText.setLike(isLike);
                int likeDrawableId = (isLike ? R.drawable.ic_fa_thumbs_up : R.drawable.ic_fa_thumbs_o_up);
                likeText.setCompoundDrawablesWithIntrinsicBounds(likeDrawableId, 0, 0, 0);
                int likes = party.getLikeCounts();
                likeText.setText(likes > 0 ? String.valueOf(likes) : getString(R.string.label_like));
                LikeListener likeListener = new LikeListener(party);
                likeListener.setOnLikeFinishedListener(new LikeListener.LikeFinishedListener() {
                    @Override
                    public void OnLikeFinished(Party party) {
                        updateLikesLayout(party.getFans());
                    }

                    @Override
                    public void OnUnLikeFinished(Party party) {
                        updateLikesLayout(party.getFans());
                    }
                });
                likeText.setLikeListener(likeListener);

                membersLayoutListener.update(membersLayout, party.getMembers());

                updateLikesLayout(party.getFans());
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void updateLikesLayout(List<String> likes) {
        try {
            likesLayoutListener.update(likesLayout, likes);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onPartyDeletedSuccess(String partyId) {
        final Intent intent = new Intent();
        intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, partyId);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onPartySharedSuccess() {

    }

    @Override
    public void onPartyActionStart() {
        dropdownImage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPartyActionFinish() {
        progressBar.setVisibility(View.GONE);
        dropdownImage.setVisibility(View.VISIBLE);
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
