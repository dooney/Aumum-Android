package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.DropdownImageView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/10/2014.
 */
public class PartyCard extends Card implements PartyActionListener.OnProgressListener{
    private Activity activity;
    private Party party;
    private String currentUserId;
    private LikeListener likeListener;
    private PartyOwnerActionListener partyOwnerActionListener;
    private PartyUserActionListener partyUserActionListener;
    private DropdownImageView dropdownImage;
    private ProgressBar progressBar;
    private ViewGroup layoutMembers;
    private TextView membersCountText;

    private UserStore userStore;

    public Party getParty() {
        return party;
    }

    public PartyCard(final Activity context, final Party party, String currentUserId,
                     PartyActionListener.OnActionListener onActionListener) {
        super(context, R.layout.party_listitem_inner);
        this.activity = context;
        this.party = party;
        this.currentUserId = currentUserId;
        this.likeListener = new LikeListener(party);
        this.partyOwnerActionListener = new PartyOwnerActionListener(party);
        this.partyOwnerActionListener.setOnActionListener(onActionListener);
        this.partyOwnerActionListener.setOnProgressListener(this);
        this.partyUserActionListener = new PartyUserActionListener(party);
        this.partyUserActionListener.setOnActionListener(onActionListener);
        this.partyUserActionListener.setOnProgressListener(this);
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                startPartyDetailsActivity();
            }
        });

        this.userStore = UserStore.getInstance(context);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(party.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));

        dropdownImage = (DropdownImageView) view.findViewById(R.id.image_dropdown);
        if (party.isOwner(currentUserId)) {
            dropdownImage.init(partyOwnerActionListener);
        } else {
            dropdownImage.init(partyUserActionListener);
        }

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(party.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), party.getUserId()));

        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        titleText.setText(party.getTitle());

        TextView distanceText = (TextView) view.findViewById(R.id.text_distance);
        distanceText.setText(view.getResources().getString(R.string.label_distance, party.getDistance()));

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(party.getCreatedAtFormatted());

        TextView dateText = (TextView) view.findViewById(R.id.text_date);
        dateText.setText(party.getDate().getDateText());

        TextView timeText = (TextView) view.findViewById(R.id.text_time);
        timeText.setText(party.getTime().getTimeText());

        TextView locationText = (TextView) view.findViewById(R.id.text_location);
        locationText.setText(party.getPlace().getLocation());

        TextView ageText = (TextView) view.findViewById(R.id.text_age);
        ageText.setText(Constants.Options.AGE_OPTIONS[party.getAge()]);

        TextView genderText = (TextView) view.findViewById(R.id.text_gender);
        genderText.setText(Constants.Options.GENDER_OPTIONS[party.getGender()]);

        ViewGroup joinLayout = (ViewGroup) view.findViewById(R.id.layout_join);
        if (party.isOwner(currentUserId)) {
            joinLayout.setVisibility(View.GONE);
        } else {
            joinLayout.setVisibility(View.VISIBLE);
            JoinTextView joinText = (JoinTextView) view.findViewById(R.id.text_join);
            TextView expiredText = (TextView) view.findViewById(R.id.text_expired);
            if (party.isExpired()) {
                joinText.setVisibility(View.GONE);
                expiredText.setVisibility(View.VISIBLE);
            } else {
                expiredText.setVisibility(View.GONE);
                joinText.setVisibility(View.VISIBLE);
                joinText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation.animateTextView(view);
                        startPartyDetailsActivity();
                    }
                });
                joinText.update(party.isMember(currentUserId));
            }
        }

        TextView commentText = (TextView) view.findViewById(R.id.text_comment);
        int comments = party.getCommentCounts();
        commentText.setText(comments > 0 ? String.valueOf(comments) : view.getResources().getString(R.string.label_comment));
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation.animateTextView(view);
                final Intent intent = new Intent(activity, PartyCommentsActivity.class);
                intent.putExtra(PartyCommentsActivity.INTENT_PARTY_ID, party.getObjectId());
                activity.startActivity(intent);
            }
        });

        LikeTextView likeText = (LikeTextView) view.findViewById(R.id.text_like);
        boolean isLike = party.isLike(currentUserId);
        likeText.setLike(isLike);
        int likeDrawableId = (isLike ? R.drawable.ic_fa_thumbs_up : R.drawable.ic_fa_thumbs_o_up);
        likeText.setCompoundDrawablesWithIntrinsicBounds(likeDrawableId, 0, 0, 0);
        int likes = party.getLikes();
        likeText.setText(likes > 0 ? String.valueOf(likes) : view.getResources().getString(R.string.label_like));
        likeText.setLikeListener(likeListener);

        layoutMembers = (ViewGroup) view.findViewById(R.id.layout_members);
        membersCountText = (TextView) view.findViewById(R.id.text_members_count);
        updateMembersLayout(party.getMembers());
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

    private void updateMembersLayout(List<String> members) {
        int count = members.size();
        if (count > 0) {
            ViewGroup layoutMembersAvatars = (ViewGroup) layoutMembers.findViewById(R.id.layout_members_avatars);
            layoutMembersAvatars.setVisibility(View.VISIBLE);
            LayoutInflater inflater = activity.getLayoutInflater();

            layoutMembersAvatars.removeAllViews();
            for(String userId: members) {
                if (!userId.equals(currentUserId)) {
                    AvatarImageView imgAvatar = (AvatarImageView) inflater.inflate(R.layout.small_avatar, layoutMembersAvatars, false);
                    imgAvatar.setOnClickListener(new UserListener(activity, userId));
                    User user = userStore.getUserById(userId, false);
                    imgAvatar.getFromUrl(user.getAvatarUrl());
                    layoutMembersAvatars.addView(imgAvatar);
                }
            }

            if (members.contains(currentUserId)) {
                if (count == 1) {
                    membersCountText.setText(activity.getString(R.string.label_you_join_the_party));
                    layoutMembersAvatars.setVisibility(View.GONE);
                } else {
                    membersCountText.setText(activity.getString(R.string.label_you_and_others_join_the_party, count - 1));
                }
            } else {
                membersCountText.setText(activity.getString(R.string.label_others_join_the_party, count));
            }

            if (layoutMembers.getVisibility() != View.VISIBLE) {
                Animation.fadeIn(layoutMembers, Animation.Duration.SHORT);
            }
        }
    }

    private void startPartyDetailsActivity() {
        final Intent intent = new Intent(activity, PartyDetailsActivity.class);
        intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, party.getObjectId());
        activity.startActivityForResult(intent, Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE);
    }
}
