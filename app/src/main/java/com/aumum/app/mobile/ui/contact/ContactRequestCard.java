package com.aumum.app.mobile.ui.contact;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.github.kevinsawicki.wishlist.Toaster;

/**
 * Created by Administrator on 20/11/2014.
 */
public class ContactRequestCard implements AcceptContactListener.OnActionListener,
        AcceptContactListener.OnProgressListener{

    private Activity activity;
    private ViewGroup layout;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView introText;
    private Button acceptButton;
    private TextView addedText;
    private ProgressBar progressBar;

    public ContactRequestCard(Activity activity, View view) {
        this.activity = activity;
        layout = (ViewGroup) view.findViewById(R.id.layout_contact_request);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        introText = (TextView) view.findViewById(R.id.text_intro);
        acceptButton = (Button) view.findViewById(R.id.b_accept);
        addedText = (TextView) view.findViewById(R.id.text_added);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }

    public void refresh(ContactRequest request) {
        User user = request.getUser();
        layout.setOnClickListener(new UserListener(activity, user.getObjectId()));
        avatarImage.getFromUrl(user.getAvatarUrl());
        screenNameText.setText(user.getScreenName());
        introText.setText(request.getIntro());

        acceptButton.setVisibility(View.GONE);
        addedText.setVisibility(View.GONE);
        if (request.isAdded()) {
            addedText.setVisibility(View.VISIBLE);
        } else {
            acceptButton.setVisibility(View.VISIBLE);
            AcceptContactListener acceptContactListener = new AcceptContactListener(user.getObjectId());
            acceptContactListener.setOnActionListener(this);
            acceptContactListener.setOnProgressListener(this);
            acceptButton.setOnClickListener(acceptContactListener);
        }
    }

    @Override
    public void onAcceptContactSuccess() {
        Animation.fadeIn(addedText, Animation.Duration.SHORT);
    }

    @Override
    public void onAcceptContactFailed() {
        Animation.fadeIn(acceptButton, Animation.Duration.SHORT);
        Toaster.showShort(activity, R.string.error_accept_contact);
    }

    @Override
    public void onAcceptContactStart() {
        acceptButton.setVisibility(View.GONE);
        addedText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAcceptContactFinish() {
        progressBar.setVisibility(View.GONE);
    }
}
