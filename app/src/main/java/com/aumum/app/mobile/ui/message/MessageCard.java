package com.aumum.app.mobile.ui.message;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.MessageParent;
import com.aumum.app.mobile.ui.party.PartyDetailsActivity;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 8/10/2014.
 */
public class MessageCard extends Card
        implements DeleteMessageListener.OnProgressListener {
    private Activity activity;
    private Message message;
    private DeleteMessageListener deleteMessageListener;
    private ImageView deleteImage;
    private ProgressBar progressBar;

    public Message getMessage() {
        return message;
    }

    public MessageCard(Activity activity, Message message, String currentUserId,
                       DeleteMessageListener.OnActionListener onActionListener) {
        super(activity, R.layout.message_listitem_inner);
        this.activity = activity;
        this.message = message;
        this.deleteMessageListener = new DeleteMessageListener(message, currentUserId);
        this.deleteMessageListener.setOnActionListener(onActionListener);
        this.deleteMessageListener.setOnProgressListener(this);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);

        AvatarImageView avatarImage = (AvatarImageView)view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(message.getFromUser().getAvatarUrl());
        avatarImage.setOnClickListener(new UserListener(avatarImage.getContext(), message.getFromUserId()));

        deleteImage = (ImageView) view.findViewById(R.id.image_delete);
        deleteImage.setOnClickListener(deleteMessageListener);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        TextView userName = (TextView)view.findViewById(R.id.text_user_name);
        userName.setText(message.getFromUser().getScreenName());

        TextView content = (TextView) view.findViewById(R.id.text_content);
        if (message.getContent() != null) {
            content.setText(message.getContent());
            content.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.GONE);
        }

        TextView createdAt = (TextView)view.findViewById(R.id.text_createdAt);
        createdAt.setText(message.getCreatedAtFormatted());

        TextView action = (TextView) view.findViewById(R.id.text_action);
        action.setText(message.getActionText());

        ViewGroup layoutParentBox = (ViewGroup) view.findViewById(R.id.layout_parent_box);
        if (message.getParent() != null) {
            updateMessageParent(view);
            layoutParentBox.setVisibility(View.VISIBLE);
        } else {
            layoutParentBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeleteMessageStart() {
        deleteImage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleteMessageFinish() {
        progressBar.setVisibility(View.GONE);
        deleteImage.setVisibility(View.VISIBLE);
    }

    private void updateMessageParent(View view) {
        final MessageParent parent = message.getParent();
        TextView content = (TextView) view.findViewById(R.id.text_parent_content);
        content.setText(parent.getContent());
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(activity, PartyDetailsActivity.class);
                intent.putExtra(PartyDetailsActivity.INTENT_PARTY_ID, parent.getObjectId());
                activity.startActivityForResult(intent, Constants.RequestCode.GET_PARTY_DETAILS_REQ_CODE);
            }
        });
    }
}
