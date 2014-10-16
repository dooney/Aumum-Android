package com.aumum.app.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.authenticator.ApiKeyProvider;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.Party;
import com.aumum.app.mobile.core.PartyStore;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.ui.view.Animation;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 16/10/2014.
 */
public class PartyDetailsFragment extends LoaderFragment<Party> {
    @Inject ApiKeyProvider apiKeyProvider;
    private String partyId;
    private String currentUserId;

    private PartyStore partyStore;
    private UserStore userStore;

    private View mainView;
    private ImageView avatarImage;
    private TextView areaText;
    private TextView userNameText;
    private TextView titleText;
    private TextView createdAtText;
    private TextView timeText;
    private TextView locationText;
    private TextView ageText;
    private TextView genderText;
    private TextView detailsText;
    private ViewGroup layoutMembers;
    private TextView membersCountText;
    private ViewGroup layoutLikes;
    private TextView likesCountText;

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

        mainView = view.findViewById(R.id.main_view);
        avatarImage = (ImageView) view.findViewById(R.id.image_avatar);
        areaText = (TextView) view.findViewById(R.id.text_area);
        userNameText = (TextView) view.findViewById(R.id.text_user_name);
        titleText = (TextView) view.findViewById(R.id.text_title);
        createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        timeText = (TextView) view.findViewById(R.id.text_time);
        locationText = (TextView) view.findViewById(R.id.text_location);
        ageText = (TextView) view.findViewById(R.id.text_age);
        genderText = (TextView) view.findViewById(R.id.text_gender);
        detailsText = (TextView) view.findViewById(R.id.text_details);

        layoutMembers = (ViewGroup) view.findViewById(R.id.layout_members);
        membersCountText = (TextView) view.findViewById(R.id.text_members_count);
        layoutLikes = (ViewGroup) view.findViewById(R.id.layout_likes);
        likesCountText = (TextView) view.findViewById(R.id.text_likes_count);
    }

    @Override
    public void onDestroyView() {
        mainView = null;

        super.onDestroyView();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_party;
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
        if (currentUserId.equals(party.getUserId())) {
            user = userStore.getCurrentUser(false);
        } else {
            user = userStore.getUserById(party.getUserId(), false);
        }
        party.setUser(user);
        return party;
    }

    @Override
    protected void handleLoadResult(Party party) {
        setData(party);

        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));
        areaText.setText(Constants.AREA_OPTIONS[party.getArea()]);
        userNameText.setText(party.getUser().getUsername());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), party.getUserId()));
        titleText.setText(party.getTitle());
        createdAtText.setText("5分钟前");
        timeText.setText("2014年10月1号 上午11点半");
        locationText.setText(party.getLocation());
        ageText.setText(Constants.AGE_OPTIONS[party.getAge()]);
        genderText.setText(Constants.GENDER_OPTIONS[party.getGender()]);
        detailsText.setText(party.getDetails());

        updateMembersLayout(party.getMembers());
        updateLikesLayout(party.getFans());
    }

    private void updateMembersLayout(List<String> members) {
        int count = members.size();
        if (count > 0) {
            ViewGroup layoutMembersAvatars = (ViewGroup) layoutMembers.findViewById(R.id.layout_members_avatars);
            LayoutInflater inflater = getActivity().getLayoutInflater();

            for(String userId: members) {
                ImageView imgAvatar = (ImageView) inflater.inflate(R.layout.small_avatar, layoutMembersAvatars, false);
                layoutMembersAvatars.addView(imgAvatar);
            }

            if (members.contains(currentUserId)) {
                if (count == 1) {
                    membersCountText.setText(getString(R.string.label_you_join_the_party));
                } else {
                    membersCountText.setText(getString(R.string.label_you_and_others_join_the_party, count - 1));
                }
            } else {
                membersCountText.setText(getString(R.string.label_others_join_the_party, count));
            }

            if (layoutMembers.getVisibility() != View.VISIBLE) {
                Animation.fadeIn(layoutMembers, Animation.Duration.SHORT);
            }
        }
    }

    private void updateLikesLayout(List<String> likes) {
        int count = likes.size();
        if (count > 0) {
            ViewGroup layoutLikingAvatars = (ViewGroup) layoutLikes.findViewById(R.id.layout_liking_avatars);
            LayoutInflater inflater = getActivity().getLayoutInflater();

            for(String userId: likes) {
                ImageView imgAvatar = (ImageView) inflater.inflate(R.layout.small_avatar, layoutLikingAvatars, false);
                layoutLikingAvatars.addView(imgAvatar);
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
}
