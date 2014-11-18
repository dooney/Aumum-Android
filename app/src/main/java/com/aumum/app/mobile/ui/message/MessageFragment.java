package com.aumum.app.mobile.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.model.Message;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MessageFragment extends Fragment {

    @Inject MessageStore messageStore;

    private long unreadPartyMembershipCount;
    private long unreadPartyCommentsCount;
    private long unreadPartyLikesCount;

    private ImageView partyMembershipUnreadImage;
    private ImageView partyCommentsUnreadImage;
    private ImageView partyLikesUnreadImage;

    private static final int MESSAGE_LIST_REQ_CODE = 100;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView partyMembershipText = (TextView) view.findViewById(R.id.text_party_membership);
        partyMembershipUnreadImage = (ImageView) view.findViewById(R.id.image_unread_party_membership_message);
        partyMembershipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int subCategory = Message.SubCategory.PARTY_MEMBERSHIP;
                startMessageListActivity(subCategory, R.string.label_party_join);
                messageStore.markAsRead(Message.getSubCategoryTypes(subCategory));
            }
        });

        TextView partyCommentsText = (TextView) view.findViewById(R.id.text_party_comments);
        partyCommentsUnreadImage = (ImageView) view.findViewById(R.id.image_unread_party_comments_message);
        partyCommentsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int subCategory = Message.SubCategory.PARTY_COMMENTS;
                startMessageListActivity(subCategory, R.string.label_party_comments);
                messageStore.markAsRead(Message.getSubCategoryTypes(subCategory));
            }
        });

        TextView partyLikesText = (TextView) view.findViewById(R.id.text_party_likes);
        partyLikesUnreadImage = (ImageView) view.findViewById(R.id.image_unread_party_likes_message);
        partyLikesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int subCategory = Message.SubCategory.PARTY_LIKES;
                startMessageListActivity(subCategory, R.string.label_party_likes);
                messageStore.markAsRead(Message.getSubCategoryTypes(subCategory));
            }
        });

        updateUnreadView(messageStore.getUnreadList());
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == MESSAGE_LIST_REQ_CODE) {
            updateUnreadView(messageStore.getUnreadList());
        }
    }

    private void startMessageListActivity(int category, int title) {
        final Intent intent = new Intent(getActivity(), MessageListActivity.class);
        intent.putExtra(MessageListActivity.INTENT_TITLE, title);
        intent.putExtra(MessageListActivity.INTENT_MESSAGE_TYPE, category);
        startActivityForResult(intent, MESSAGE_LIST_REQ_CODE);
    }

    private void toggleUnreadImage(ImageView imageView, long count) {
        if (count > 0) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private void updateUnreadView(List<Message> unreadList) {
        unreadPartyMembershipCount = 0;
        unreadPartyCommentsCount = 0;
        unreadPartyLikesCount = 0;
        List<Integer> partyMembershipTypes = Message.getSubCategoryTypes(Message.SubCategory.PARTY_MEMBERSHIP);
        List<Integer> partyCommentsTypes = Message.getSubCategoryTypes(Message.SubCategory.PARTY_COMMENTS);
        List<Integer> partyLikesTypes = Message.getSubCategoryTypes(Message.SubCategory.PARTY_LIKES);
        for (Message message: unreadList) {
            if (partyMembershipTypes.contains(message.getType())) {
                unreadPartyMembershipCount++;
            } else if (partyCommentsTypes.contains(message.getType())) {
                unreadPartyCommentsCount++;
            } else if (partyLikesTypes.contains(message.getType())) {
                unreadPartyLikesCount++;
            }
        }

        toggleUnreadImage(partyMembershipUnreadImage, unreadPartyMembershipCount);
        toggleUnreadImage(partyCommentsUnreadImage, unreadPartyCommentsCount);
        toggleUnreadImage(partyLikesUnreadImage, unreadPartyLikesCount);
    }
}
