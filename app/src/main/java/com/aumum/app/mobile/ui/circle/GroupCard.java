package com.aumum.app.mobile.ui.circle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.easemob.chat.EMGroup;

/**
 * Created by Administrator on 10/11/2014.
 */
public class GroupCard implements GroupActionListener.OnProgressListener {

    private Activity activity;
    private View view;
    private String currentUserId;
    private ImageView avatarImage;
    private TextView screenNameText;
    private TextView currentSizeText;
    private Button actionButton;
    private ProgressBar progressBar;

    public GroupCard(Activity activity, View view, String currentUserId) {
        this.activity = activity;
        this.view = view;
        this.currentUserId = currentUserId;
        this.avatarImage = (ImageView) view.findViewById(R.id.image_avatar);
        this.screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        this.currentSizeText = (TextView) view.findViewById(R.id.text_current_size);
        this.actionButton = (Button) view.findViewById(R.id.b_action);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(final EMGroup group) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (group.getMembers().contains(currentUserId)) {
                    final Intent intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra(ChatActivity.INTENT_TITLE, group.getGroupName());
                    intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_GROUP);
                    intent.putExtra(ChatActivity.INTENT_ID, group.getGroupId());
                    activity.startActivity(intent);
                }
            }
        });

        screenNameText.setText(group.getGroupName());
        currentSizeText.setText(activity.getString(R.string.label_group_current_size, group.getMembers().size()));

        updateAction(group);
        GroupActionListener listener = new GroupActionListener(activity, group);
        listener.setOnProgressListener(this);
        actionButton.setOnClickListener(listener);
    }

    private void updateAction(EMGroup group) {
        if (group.getMembers().contains(currentUserId)) {
            actionButton.setText(R.string.label_quit_group);
            actionButton.setBackgroundResource(R.drawable.bbuton_danger_rounded);
        } else {
            actionButton.setText(R.string.label_join_group);
            actionButton.setBackgroundResource(R.drawable.bbuton_primary_rounded);
        }
    }

    @Override
    public void onActionStart() {
        actionButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActionSuccess(EMGroup group) {
        updateAction(group);
    }

    @Override
    public void onActionFinish() {
        progressBar.setVisibility(View.GONE);
        actionButton.setVisibility(View.VISIBLE);
    }
}
