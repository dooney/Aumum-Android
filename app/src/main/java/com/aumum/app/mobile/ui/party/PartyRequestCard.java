package com.aumum.app.mobile.ui.party;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.PartyRequest;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyRequestCard extends Card {

    private Activity activity;
    private PartyRequest partyRequest;
    private String currentUserId;
    private PartyRequestDeleteListener partyRequestDeleteListener;
    private PartyRequestMessagingListener partyRequestMessagingListener;

    public PartyRequest getPartyRequest() {
        return partyRequest;
    }

    public PartyRequestCard(Activity activity,
                            PartyRequest partyRequest,
                            String currentUserId,
                            PartyRequestDeleteListener partyRequestDeleteListener,
                            PartyRequestMessagingListener partyRequestMessagingListener) {
        super(activity, R.layout.party_request_listitem_inner);
        this.activity = activity;
        this.partyRequest = partyRequest;
        this.currentUserId = currentUserId;
        this.partyRequestDeleteListener = partyRequestDeleteListener;
        this.partyRequestMessagingListener = partyRequestMessagingListener;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        final User user = partyRequest.getUser();

        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(partyRequest.getUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), partyRequest.getUserId()));

        TextView userNameText = (TextView) view.findViewById(R.id.text_user_name);
        userNameText.setText(user.getScreenName());
        userNameText.setOnClickListener(new UserListener(userNameText.getContext(), partyRequest.getUserId()));

        TextView cityText = (TextView) view.findViewById(R.id.text_city);
        cityText.setText(partyRequest.getUser().getCity());

        TextView createdAtText = (TextView) view.findViewById(R.id.text_createdAt);
        createdAtText.setText(partyRequest.getCreatedAtFormatted());

        ImageView messagingText = (ImageView) view.findViewById(R.id.image_messaging);
        if (partyRequest.isOwner(currentUserId)) {
            messagingText.setVisibility(View.GONE);
        } else {
            messagingText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startChatActivity(user);
                    if (partyRequestMessagingListener != null) {
                        partyRequestMessagingListener.onMessaging(partyRequest);
                    }
                }
            });
            messagingText.setVisibility(View.VISIBLE);
        }

        ImageView deleteImage = (ImageView) view.findViewById(R.id.image_delete);
        if (partyRequest.isOwner(currentUserId)) {
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (partyRequestDeleteListener != null) {
                        partyRequestDeleteListener.onDelete(partyRequest);
                    }
                }
            });
            deleteImage.setVisibility(View.VISIBLE);
        } else {
            deleteImage.setVisibility(View.GONE);
        }

        TextView detailsText = (TextView) view.findViewById(R.id.text_details);
        detailsText.setText(partyRequest.getDetails());
    }

    private void startChatActivity(User user) {
        final Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.INTENT_TITLE, user.getScreenName());
        intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
        intent.putExtra(ChatActivity.INTENT_ID, user.getChatId());
        activity.startActivity(intent);
    }
}
