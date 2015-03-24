package com.aumum.app.mobile.ui.conversation;

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

    private View view;

    public GroupCard(View view) {
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

        Button addButton = (Button) view.findViewById(R.id.b_add);
        TextView addedText = (TextView) view.findViewById(R.id.text_added);
        addButton.setVisibility(View.GONE);
        addedText.setVisibility(View.GONE);
        if (groupDetails.isAdded()) {
            addedText.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
        }
    }
}
