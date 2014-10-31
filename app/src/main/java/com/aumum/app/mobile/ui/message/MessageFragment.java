package com.aumum.app.mobile.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Message;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MessageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView partyMembershipText = (TextView) view.findViewById(R.id.text_party_membership);
        partyMembershipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMessageListActivity(Message.SubCategory.PARTY_MEMBERSHIP);
            }
        });

        TextView partyCommentsText = (TextView) view.findViewById(R.id.text_party_comments);
        partyCommentsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMessageListActivity(Message.SubCategory.PARTY_COMMENTS);
            }
        });

        TextView partyLikesText = (TextView) view.findViewById(R.id.text_party_likes);
        partyLikesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMessageListActivity(Message.SubCategory.PARTY_LIKES);
            }
        });
    }

    private void startMessageListActivity(int category) {
        final Intent intent = new Intent(getActivity(), MessageListActivity.class);
        intent.putExtra(MessageListActivity.INTENT_MESSAGE_TYPE, category);
        startActivity(intent);
    }
}
