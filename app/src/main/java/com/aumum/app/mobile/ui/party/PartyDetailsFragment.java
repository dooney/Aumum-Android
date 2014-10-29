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
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.events.AddPartyJoinReasonEvent;
import com.aumum.app.mobile.events.AddPartyJoinReasonFinishedEvent;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.DropdownImageView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.QuickReturnScrollView;
import com.aumum.app.mobile.utils.EditTextUtils;
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

    private String partyId;
    private String currentUserId;

    private PartyStore partyStore;
    private UserStore userStore;

    private QuickReturnScrollView scrollView;
    private View mainView;
    private DropdownImageView dropdownImage;
    private ProgressBar progressBar;
    private AvatarImageView avatarImage;
    private TextView areaText;
    private TextView userNameText;
    private TextView titleText;
    private TextView createdAtText;
    private TextView dateText;
    private TextView timeText;
    private TextView locationText;
    private TextView ageText;
    private TextView genderText;
    private TextView detailsText;
    private ViewGroup layoutLikes;
    private TextView likesCountText;

    private ViewGroup layoutActions;
    private ViewGroup layoutJoinBox;
    private JoinTextView joinText;
    private EditText editJoinReason;
    private ImageView postJoinReasonButton;
    private boolean isJoinBoxShow;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        partyStore = new PartyStore(getActivity());
        userStore = UserStore.getInstance(getActivity());
        final Intent intent = getActivity().getIntent();
        partyId = intent.getStringExtra(PartyDetailsActivity.INTENT_PARTY_ID);
        currentUserId = apiKeyProvider.getAuthUserId();
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
        areaText = (TextView) view.findViewById(R.id.text_area);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        titleText = (TextView) view.findViewById(R.id.text_title);
        createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        dateText = (TextView) view.findViewById(R.id.text_date);
        timeText = (TextView) view.findViewById(R.id.text_time);
        locationText = (TextView) view.findViewById(R.id.text_location);
        ageText = (TextView) view.findViewById(R.id.text_age);
        genderText = (TextView) view.findViewById(R.id.text_gender);
        detailsText = (TextView) view.findViewById(R.id.text_details);

        layoutLikes = (ViewGroup) view.findViewById(R.id.layout_likes);
        likesCountText = (TextView) view.findViewById(R.id.text_likes_count);

        layoutActions = (ViewGroup) view.findViewById(R.id.layout_actions);
        layoutJoinBox = (ViewGroup) view.findViewById(R.id.layout_join_box);
        joinText = (JoinTextView) view.findViewById(R.id.text_join);
        joinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleJoinBox();
            }
        });
        editJoinReason = (EditText) view.findViewById(R.id.edit_join_reason);
        postJoinReasonButton = (ImageView) view.findViewById(R.id.image_post_join_reason);
        postJoinReasonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitJoin();
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
        Party party = partyStore.getPartyById(partyId);
        if (party == null) {
            throw new Exception(getString(R.string.invalid_party));
        }
        User user;
        if (party.isOwner(currentUserId)) {
            user = userStore.getCurrentUser(false);
        } else {
            user = userStore.getUserById(party.getUserId(), false);
        }
        party.setUser(user);
        return party;
    }

    @Override
    protected void handleLoadResult(Party party) {
        try {
            if (party != null) {
                setData(party);

                avatarImage.getFromUrl(party.getUser().getAvatarUrl());
                avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));

                if (party.isOwner(currentUserId)) {
                    PartyOwnerActionListener listener = new PartyOwnerActionListener(party);
                    listener.setOnActionListener(this);
                    listener.setOnProgressListener(this);
                    dropdownImage.init(listener);
                } else {
                    PartyUserActionListener listener = new PartyUserActionListener(party);
                    listener.setOnActionListener(this);
                    listener.setOnProgressListener(this);
                    dropdownImage.init(listener);
                }

                areaText.setText(Constants.Options.AREA_OPTIONS[party.getArea()]);
                userNameText.setText(party.getUser().getScreenName());
                userNameText.setOnClickListener(new UserListener(userNameText.getContext(), party.getUserId()));
                titleText.setText(party.getTitle());
                createdAtText.setText(party.getCreatedAtFormatted());
                dateText.setText(party.getDate().getDateText());
                timeText.setText(party.getTime().getTimeText());
                locationText.setText(party.getLocation());
                ageText.setText(Constants.Options.AGE_OPTIONS[party.getAge()]);
                genderText.setText(Constants.Options.GENDER_OPTIONS[party.getGender()]);
                detailsText.setText(party.getDetails());
                joinText.update(party.isMember(currentUserId));

                updateLikesLayout(party.getFans());
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void updateLikesLayout(List<String> likes) {
        int count = likes.size();
        if (count > 0) {
            ViewGroup layoutLikingAvatars = (ViewGroup) layoutLikes.findViewById(R.id.layout_liking_avatars);
            LayoutInflater inflater = getActivity().getLayoutInflater();

            for(String userId: likes) {
                if (!userId.equals(currentUserId)) {
                    AvatarImageView imgAvatar = (AvatarImageView) inflater.inflate(R.layout.small_avatar, layoutLikingAvatars, false);
                    imgAvatar.setOnClickListener(new UserListener(getActivity(), userId));
                    User user = userStore.getUserById(userId, false);
                    imgAvatar.getFromUrl(user.getAvatarUrl());
                    layoutLikingAvatars.addView(imgAvatar);
                }
            }

            if (likes.contains(currentUserId)) {
                if (count == 1) {
                    likesCountText.setText(getString(R.string.label_you_like_the_party));
                } else {
                    likesCountText.setText(getString(R.string.label_you_and_others_like_the_party, count - 1));
                }
            } else {
                likesCountText.setText(getString(R.string.label_others_like_the_party, count));
            }

            if (layoutLikes.getVisibility() != View.VISIBLE) {
                Animation.fadeIn(layoutLikes, Animation.Duration.SHORT);
            }
        }
    }

    @Override
    public void onPartyDeletedSuccess(String partyId) {
        final Intent intent = new Intent();
        intent.putExtra(PartyDetailsActivity.INTENT_PARTY_DELETED, true);
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
        EditTextUtils.hideSoftInput(editJoinReason);
        editJoinReason.setText(null);
        Animation.flyOut(layoutJoinBox);
    }

    private void showJoinBox() {
        Animation.flyIn(layoutJoinBox);
        EditTextUtils.showSoftInput(editJoinReason, true);
    }

    private void toggleJoinBox() {
        if (isJoinBoxShow) {
            hideJoinBox();
        } else {
            editJoinReason.setHint(R.string.hint_join_reason);
            showJoinBox();
        }
        isJoinBoxShow = !isJoinBoxShow;
    }

    private void enableSubmit() {
        postJoinReasonButton.setEnabled(true);
    }

    private void disableSubmit() {
        postJoinReasonButton.setEnabled(false);
    }

    private void submitJoin() {
        String reason = editJoinReason.getText().toString();
        bus.post(new AddPartyJoinReasonEvent(reason));

        hideJoinBox();
        disableSubmit();
    }

    @Subscribe
    public void onAddPartyJoinReasonFinishedEvent(AddPartyJoinReasonFinishedEvent event) {
        enableSubmit();
    }

    @Override
    public void onScrollUp() {
        if (!isJoinBoxShow) {
            Animation.animateIconBar(layoutActions, true);
        }
    }

    @Override
    public void onScrollDown() {
        if (isJoinBoxShow) {
            return;
        }

        boolean canScrollDown = scrollView.canScrollDown();
        boolean canScrollUp = scrollView.canScrollUp();
        if (!canScrollDown) {
            Animation.animateIconBar(layoutActions, true);
        } else if (canScrollDown && canScrollUp) {
            Animation.animateIconBar(layoutActions, false);
        }
    }
}
