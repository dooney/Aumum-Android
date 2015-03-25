package com.aumum.app.mobile.ui.conversation;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.GroupDetails;
import com.aumum.app.mobile.core.model.User;
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

    public void refresh(GroupDetails groupDetails) {
        TextView groupNameText = (TextView) view.findViewById(R.id.text_group_name);
        groupNameText.setText(groupDetails.getName());

        User user = groupDetails.getOwner();
        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(user.getScreenName());
        screenNameText.setOnClickListener(new UserListener(view.getContext(), user.getObjectId()));

        TextView groupSizeText = (TextView) view.findViewById(R.id.text_group_size);
        groupSizeText.setText(String.valueOf(groupDetails.getSize()));

        Button joinButton = (Button) view.findViewById(R.id.b_join);
        TextView jointText = (TextView) view.findViewById(R.id.text_joint);
        joinButton.setVisibility(View.GONE);
        jointText.setVisibility(View.GONE);
        if (groupDetails.isMember()) {
            jointText.setVisibility(View.VISIBLE);
        } else {
            joinButton.setOnClickListener(new GroupJoinListener(activity, groupDetails.getId()));
            joinButton.setVisibility(View.VISIBLE);
        }
    }
}
