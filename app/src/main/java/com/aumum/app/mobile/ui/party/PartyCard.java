package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.DropdownImageView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/10/2014.
 */
public class PartyCard extends Card implements PartyActionListener.OnProgressListener{
    private Activity activity;
    private Party party;
    private String currentUserId;
    private LikeListener likeListener;
    private MembersLayoutListener membersLayoutListener;
    private PartyDetailsListener detailsListener;
    private PartyOwnerActionListener ownerActionListener;
    private PartyUserActionListener userActionListener;
    private DropdownImageView dropdownImage;
    private ProgressBar progressBar;

    public Party getParty() {
        return party;
    }

    public PartyCard(final Activity activity, final Party party, String currentUserId,
                     PartyActionListener.OnActionListener onActionListener,
                     PartyDetailsListener partyDetailsListener) {
        super(activity, R.layout.party_listitem_inner);
        this.activity = activity;
        this.party = party;
        this.currentUserId = currentUserId;
        this.likeListener = new LikeListener(party);
        this.membersLayoutListener = new MembersLayoutListener(activity, currentUserId);
        this.detailsListener = partyDetailsListener;
        this.ownerActionListener = new PartyOwnerActionListener(activity, party);
        this.ownerActionListener.setOnActionListener(onActionListener);
        this.ownerActionListener.setOnProgressListener(this);
        this.userActionListener = new PartyUserActionListener(activity, party);
        this.userActionListener.setOnActionListener(onActionListener);
        this.userActionListener.setOnProgressListener(this);
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                detailsListener.onPartyDetails(party.getObjectId());
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(party.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));

        dropdownImage = (DropdownImageView) view.findViewById(R.id.image_dropdown);
        if (party.isOwner(currentUserId)) {
            dropdownImage.init(ownerActionListener);
        } else {
            dropdownImage.init(userActionListener);
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
        JoinTextView joinText = (JoinTextView) view.findViewById(R.id.text_join);
        joinLayout.setVisibility(View.GONE);
        joinText.setVisibility(View.GONE);
        if (!party.isExpired() && !party.isMember(currentUserId)) {
            joinLayout.setVisibility(View.VISIBLE);
            joinText.setVisibility(View.VISIBLE);
            joinText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation.animateTextView(view);
                    detailsListener.onPartyDetails(party.getObjectId());
                }
            });
            joinText.update(party.isMember(currentUserId));
        }

        TextView commentText = (TextView) view.findViewById(R.id.text_comment);
        int comments = party.getCommentsCount();
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
        int likes = party.getLikesCount();
        likeText.setText(likes > 0 ? String.valueOf(likes) : view.getResources().getString(R.string.label_like));
        likeText.setLikeListener(likeListener);

        try {
            ViewGroup membersLayout = (ViewGroup) view.findViewById(R.id.layout_members);
            membersLayoutListener.update(membersLayout, party.getMembers());
        } catch (Exception e) {
            Ln.e(e);
        }
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
}
