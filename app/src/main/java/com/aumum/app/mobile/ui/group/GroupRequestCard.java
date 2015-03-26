package com.aumum.app.mobile.ui.group;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.GroupRequest;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 25/03/2015.
 */
public class GroupRequestCard implements GroupRequestProcessListener.OnProcessListener {

    private Activity activity;
    private ViewGroup layout;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView detailsText;
    private Button processButton;
    private TextView statusText;
    private ProgressBar progressBar;

    public GroupRequestCard(Activity activity, View view) {
        this.activity = activity;
        layout = (ViewGroup) view.findViewById(R.id.layout_group_request);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        detailsText = (TextView) view.findViewById(R.id.text_details);
        processButton = (Button) view.findViewById(R.id.b_process);
        statusText = (TextView) view.findViewById(R.id.text_status);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(GroupRequest request) {
        User user = request.getUser();
        layout.setOnClickListener(new UserListener(activity, user.getObjectId()));
        avatarImage.getFromUrl(user.getAvatarUrl());
        screenNameText.setText(user.getScreenName());
        String details = activity.getString(R.string.label_group_request_details,
                request.getGroupName(), request.getReason());
        detailsText.setText(details);
        processButton.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        switch (request.getStatus()) {
            case GroupRequest.STATUS_APPROVED:
                statusText.setText(R.string.label_approved);
                statusText.setVisibility(View.VISIBLE);
                break;
            case GroupRequest.STATUS_REJECTED:
                statusText.setText(R.string.label_rejected);
                statusText.setVisibility(View.VISIBLE);
                break;
            default:
                processButton.setOnClickListener(new GroupRequestProcessListener(activity, request, this));
                processButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onStart() {
        processButton.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onException(Exception e) {
        statusText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        processButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(boolean approve) {
        if (approve) {
            statusText.setText(R.string.label_approved);
        } else {
            statusText.setText(R.string.label_rejected);
        }
        progressBar.setVisibility(View.GONE);
        processButton.setVisibility(View.GONE);
        statusText.setVisibility(View.VISIBLE);
    }
}
