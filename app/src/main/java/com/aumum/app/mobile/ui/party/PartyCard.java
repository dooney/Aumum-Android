package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.JoinTextView;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.ui.view.SpannableTextView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2/10/2014.
 */
public class PartyCard extends Card {
    private Activity activity;
    private Fragment fragment;
    private Party party;
    private String currentUserId;
    private PartyLikeListener likeListener;
    private MembersLayoutListener membersLayoutListener;

    public Party getParty() {
        return party;
    }

    public PartyCard(final Fragment fragment, Party party, String currentUserId) {
        super(fragment.getActivity(), R.layout.party_listitem_inner);
        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.party = party;
        this.currentUserId = currentUserId;
        this.likeListener = new PartyLikeListener(party);
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

        TextView cityText = (TextView) view.findViewById(R.id.text_city);
        cityText.setText(party.getUser().getCity());

        SpannableTextView titleText = (SpannableTextView) view.findViewById(R.id.text_title);
        titleText.setSpannableText(party.getTitle());

        TextView distanceText = (TextView) view.findViewById(R.id.text_distance);
        distanceText.setText(view.getResources().getString(R.string.label_distance, party.getDistance()));

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(party.getCreatedAtFormatted());

        TextView timeText = (TextView) view.findViewById(R.id.text_time);
        timeText.setText(party.getDateTimeText());

        TextView addressText = (TextView) view.findViewById(R.id.text_address);
        String address = party.getAddress();
        if (party.getLocation() != null && party.getLocation().length() > 0) {
            address += " (" + party.getLocation() + ")";
        }
        addressText.setText(address);

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
                fragment.startActivityForResult(intent, Constants.RequestCode.GET_PARTY_COMMENTS_REQ_CODE);
            }
        });

        LikeTextView likeText = (LikeTextView) view.findViewById(R.id.text_like);
        likeText.setTextResId(R.string.label_like);
        likeText.setLikeResId(R.drawable.ic_fa_thumbs_o_up);
        likeText.setLikedResId(R.drawable.ic_fa_thumbs_up);
        likeText.init(party.getLikesCount(), party.isLiked(currentUserId));
        likeText.setLikeListener(likeListener);

        ViewGroup membersLayout = (ViewGroup) view.findViewById(R.id.layout_members);
        membersLayoutListener.update(membersLayout, party.getMembers());
    }
}
