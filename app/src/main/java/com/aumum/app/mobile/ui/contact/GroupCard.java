package com.aumum.app.mobile.ui.contact;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.easemob.chat.EMGroup;

import javax.inject.Inject;

/**
 * Created by Administrator on 10/11/2014.
 */
public class GroupCard  implements GroupJoinListener.OnProgressListener,
                                   GroupQuitListener.OnProgressListener {

    @Inject ApiKeyProvider apiKeyProvide;

    private Activity activity;
    private View view;
    private ImageView avatarImage;
    private TextView screenNameText;
    private TextView currentSizeText;
    private Button actionButton;
    private ProgressBar progressBar;

    public GroupCard(Activity activity, View view) {
        Injector.inject(this);
        this.activity = activity;
        this.view = view;
        this.avatarImage = (ImageView) view.findViewById(R.id.image_avatar);
        this.screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        this.currentSizeText = (TextView) view.findViewById(R.id.text_current_size);
        this.actionButton = (Button) view.findViewById(R.id.b_action);
        this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(final EMGroup group) {
        final String currentUserId = apiKeyProvide.getAuthUserId();

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

        if (group.getMembers().contains(currentUserId)) {
            showQuit(group);
        } else {
            showJoin(group);
        }

        showCurrentSize(group);
    }

    private void showJoin(EMGroup group) {
        actionButton.setText(R.string.label_join_group);
        actionButton.setBackgroundResource(R.drawable.bbuton_primary_rounded);
        GroupJoinListener listener = new GroupJoinListener(activity, group);
        listener.setOnProgressListener(this);
        actionButton.setOnClickListener(listener);
    }

    private void showQuit(EMGroup group) {
        actionButton.setText(R.string.label_quit_group);
        actionButton.setBackgroundResource(R.drawable.bbuton_danger_rounded);
        GroupQuitListener listener = new GroupQuitListener(activity, group);
        listener.setOnProgressListener(this);
        actionButton.setOnClickListener(listener);
    }

    private void showCurrentSize(EMGroup group) {
        currentSizeText.setText(activity.getString(R.string.label_group_current_size,
                group.getMembers().size()));
    }

    @Override
    public void onJoinStart() {
        actionButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onJoinSuccess(EMGroup group) {
        showQuit(group);
        showCurrentSize(group);
    }

    @Override
    public void onJoinFinish() {
        progressBar.setVisibility(View.GONE);
        actionButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuitStart() {
        actionButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuitSuccess(EMGroup group) {
        showJoin(group);
        showCurrentSize(group);
    }

    @Override
    public void onQuitFinish() {
        progressBar.setVisibility(View.GONE);
        actionButton.setVisibility(View.VISIBLE);
    }
}
