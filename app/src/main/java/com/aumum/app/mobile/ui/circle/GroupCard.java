package com.aumum.app.mobile.ui.circle;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Group;

/**
 * Created by Administrator on 10/11/2014.
 */
public class GroupCard implements GroupActionListener.OnProgressListener {

    private Activity activity;
    private ImageView avatarImage;
    private TextView screenNameText;
    private TextView currentSizeText;
    private Button actionButton;
    private ProgressBar progressBar;

    public GroupCard(View view, Activity activity) {
        this.activity = activity;
        this.avatarImage = (ImageView) view.findViewById(R.id.image_avatar);
        this.screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        this.currentSizeText = (TextView) view.findViewById(R.id.text_current_size);
        this.actionButton = (Button) view.findViewById(R.id.b_action);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(Group group) {
        screenNameText.setText(group.getScreenName());
        currentSizeText.setText(activity.getString(R.string.label_group_current_size, group.getCurrentSize()));

        updateAction(group.isMember());
        GroupActionListener listener = new GroupActionListener(activity, group);
        listener.setOnProgressListener(this);
        actionButton.setOnClickListener(listener);
    }

    private void updateAction(boolean isMember) {
        if (isMember) {
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
    public void onActionSuccess(Group group) {
        group.setMember(!group.isMember());
        updateAction(group.isMember());
    }

    @Override
    public void onActionFinish() {
        progressBar.setVisibility(View.GONE);
        actionButton.setVisibility(View.VISIBLE);
    }
}
