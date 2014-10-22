package com.aumum.app.mobile.ui.party;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.CommentTextView;
import com.aumum.app.mobile.ui.view.DropdownImageView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/10/2014.
 */
public class PartyCard extends Card implements PartyActionListener.OnProgressListener{
    private Party party;
    private String currentUserId;
    private JoinListener joinListener;
    private LikeListener likeListener;
    private PartyOwnerActionListener partyOwnerActionListener;
    private PartyUserActionListener partyUserActionListener;
    private DropdownImageView dropdownImage;
    private ProgressBar progressBar;

    public Party getParty() {
        return party;
    }

    public PartyCard(final Context context, final Party party, String currentUserId,
                     PartyActionListener.OnActionListener onActionListener,
                     OnCardClickListener onCardClickListener) {
        super(context, R.layout.party_listitem_inner);
        this.party = party;
        this.currentUserId = currentUserId;
        this.joinListener = new JoinListener(party);
        this.likeListener = new LikeListener(party);
        this.partyOwnerActionListener = new PartyOwnerActionListener(party);
        this.partyOwnerActionListener.setOnActionListener(onActionListener);
        this.partyOwnerActionListener.setOnProgressListener(this);
        this.partyUserActionListener = new PartyUserActionListener(party);
        this.partyUserActionListener.setOnActionListener(onActionListener);
        this.partyUserActionListener.setOnProgressListener(this);
        setOnClickListener(onCardClickListener);
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

        progressBar = (ProgressBar) view.findViewById(R.id.progress_party);

        TextView areaText = (TextView) view.findViewById(R.id.text_area);
        areaText.setText(Constants.AREA_OPTIONS[party.getArea()]);

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(party.getUser().getUsername());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), party.getUserId()));

        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        titleText.setText(party.getTitle());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(party.getCreatedAtFormatted());

        TextView timeText = (TextView) view.findViewById(R.id.text_time);
        timeText.setText("2014年10月1号 上午11点半");

        TextView locationText = (TextView) view.findViewById(R.id.text_location);
        locationText.setText(party.getLocation());

        TextView ageText = (TextView) view.findViewById(R.id.text_age);
        ageText.setText(Constants.AGE_OPTIONS[party.getAge()]);

        TextView genderText = (TextView) view.findViewById(R.id.text_gender);
        genderText.setText(Constants.GENDER_OPTIONS[party.getGender()]);

        TextView detailsText = (TextView) view.findViewById(R.id.text_details);
        detailsText.setText(party.getDetails());

        JoinTextView joinText = (JoinTextView) view.findViewById(R.id.text_join);
        boolean isJoin = party.isJoin(currentUserId);
        joinText.setJoin(isJoin);
        int joinDrawableId = (isJoin ? R.drawable.ic_fa_check : R.drawable.ic_fa_users);
        joinText.setCompoundDrawablesWithIntrinsicBounds(joinDrawableId, 0, 0, 0);
        int joins = party.getJoins();
        joinText.setText(joins > 0 ? String.valueOf(joins) : view.getResources().getString(R.string.label_join));
        joinText.setJoinListener(joinListener);

        CommentTextView commentText = (CommentTextView) view.findViewById(R.id.text_comment);
        int comments = party.getCommentCounts();
        commentText.setText(comments > 0 ? String.valueOf(comments) : view.getResources().getString(R.string.label_comment));
        commentText.setCommentListener(new PartyCommentListener(getContext(), party.getObjectId()));

        LikeTextView likeText = (LikeTextView) view.findViewById(R.id.text_like);
        boolean isLike = party.isLike(currentUserId);
        likeText.setLike(isLike);
        int likeDrawableId = (isLike ? R.drawable.ic_fa_thumbs_up : R.drawable.ic_fa_thumbs_o_up);
        likeText.setCompoundDrawablesWithIntrinsicBounds(likeDrawableId, 0, 0, 0);
        int likes = party.getLikes();
        likeText.setText(likes > 0 ? String.valueOf(likes) : view.getResources().getString(R.string.label_like));
        likeText.setLikeListener(likeListener);
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
