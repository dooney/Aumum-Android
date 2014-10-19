package com.aumum.app.mobile.ui.party;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.CommentTextView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/10/2014.
 */
public class PartyCard extends Card {
    private Party party;
    private String currentUserId;

    public PartyCard(final Context context, final Party party, String currentUserId) {
        super(context, R.layout.party_listitem_inner);
        this.party = party;
        this.currentUserId = currentUserId;
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                final Intent intent = new Intent(context, PartyDetailsActivity.class);
                intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, party.getObjectId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(party.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));

        TextView areaText = (TextView) view.findViewById(R.id.text_area);
        areaText.setText(Constants.AREA_OPTIONS[party.getArea()]);

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(party.getUser().getUsername());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), party.getUserId()));

        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        titleText.setText(party.getTitle());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText("5分钟前");

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
        joinText.setJoinListener(new JoinListener(party));

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
        likeText.setLikeListener(new LikeListener(party));
    }
}
