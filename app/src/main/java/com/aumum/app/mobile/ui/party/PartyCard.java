package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/10/2014.
 */
public class PartyCard extends Card {
    private Activity activity;
    private Party party;
    private String currentUserId;
    private LikeListener likeListener;
    private FavoriteListener favoriteListener;
    private MembersLayoutListener membersLayoutListener;

    public Party getParty() {
        return party;
    }

    public PartyCard(final Activity activity, final Party party, String currentUserId) {
        super(activity, R.layout.party_listitem_inner);
        this.activity = activity;
        this.party = party;
        this.currentUserId = currentUserId;
        this.likeListener = new LikeListener(party);
        this.favoriteListener = new FavoriteListener(party);
        this.membersLayoutListener = new MembersLayoutListener(activity, currentUserId);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(party.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), party.getUserId()));

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
                    final Intent intent = new Intent(activity, PartyDetailsActivity.class);
                    intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, party.getObjectId());
                    activity.startActivity(intent);
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
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up);
        likeText.init(party.getLikesCount(), party.isLiked(currentUserId));
        likeText.setLikeListener(likeListener);

        FavoriteTextView favoriteText = (FavoriteTextView) view.findViewById(R.id.text_favorite);
        favoriteText.setFavoriteResId(R.drawable.ic_fa_star_o);
        favoriteText.setFavoritedResId(R.drawable.ic_fa_star);
        favoriteText.init(party.getFavoritesCount(), party.isFavorited(currentUserId));
        favoriteText.setFavoriteListener(favoriteListener);

        try {
            ViewGroup membersLayout = (ViewGroup) view.findViewById(R.id.layout_members);
            membersLayoutListener.update(membersLayout, party.getMembers());
        } catch (Exception e) {
            Ln.e(e);
        }
    }
}
