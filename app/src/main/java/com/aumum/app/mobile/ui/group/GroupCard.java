package com.aumum.app.mobile.ui.group;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.GroupDetails;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.user.UserListener;

/**
 * Created by Administrator on 24/03/2015.
 */
public class GroupCard {

    private Activity activity;
    private View view;

    public GroupCard(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    public void refresh(final GroupDetails groupDetails) {
        ViewGroup layout = (ViewGroup) view.findViewById(R.id.layout_group_card);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupDetails.isMember()) {
                    startChatActivity(groupDetails);
                } else {
                    startGroupDetailsActivity(groupDetails);
                }
            }
        });

        TextView groupNameText = (TextView) view.findViewById(R.id.text_group_name);
        groupNameText.setText(groupDetails.getName());

        User user = groupDetails.getOwner();
        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(user.getScreenName());
        screenNameText.setOnClickListener(new UserListener(view.getContext(), user.getObjectId()));

        TextView groupSizeText = (TextView) view.findViewById(R.id.text_group_size);
        groupSizeText.setText(String.valueOf(groupDetails.getSize()));

        Button joinButton = (Button) view.findViewById(R.id.b_join);
        joinButton.setVisibility(View.GONE);
        if (!groupDetails.isMember()) {
            joinButton.setOnClickListener(new GroupJoinListener(activity, groupDetails.getId()));
            joinButton.setVisibility(View.VISIBLE);
        }
    }

    private void startChatActivity(GroupDetails groupDetails) {
        final Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.INTENT_TITLE, groupDetails.getName());
        intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_GROUP);
        intent.putExtra(ChatActivity.INTENT_ID, groupDetails.getId());
        activity.startActivity(intent);
    }

    private void startGroupDetailsActivity(GroupDetails groupDetails) {
        final Intent intent = new Intent(activity, GroupDetailsActivity.class);
        intent.putExtra(GroupDetailsActivity.INTENT_GROUP_ID, groupDetails.getId());
        activity.startActivity(intent);
    }
}
