package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.PartyRequest;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyRequestCard extends Card {

    private PartyRequest partyRequest;
    private String currentUserId;
    private MembersLayoutListener membersLayoutListener;

    public PartyRequestCard(Activity activity, PartyRequest partyRequest, String currentUserId) {
        super(activity, R.layout.party_request_listitem_inner);
        this.partyRequest = partyRequest;
        this.currentUserId = currentUserId;
        this.membersLayoutListener = new MembersLayoutListener(activity, currentUserId);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(partyRequest.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), partyRequest.getUserId()));

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(partyRequest.getUser().getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), partyRequest.getUserId()));

        TextView cityText = (TextView) view.findViewById(R.id.text_city);
        cityText.setText(partyRequest.getUser().getCity());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(partyRequest.getCreatedAtFormatted());

        ViewGroup membersLayout = (ViewGroup) view.findViewById(R.id.layout_members);
        membersLayoutListener.update(membersLayout, partyRequest.getMembers());
    }
}
