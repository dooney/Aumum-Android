package com.aumum.app.mobile.ui.user;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 25/12/2014.
 */
public class UserPickerCard {

    private TextView catalogText;
    private ViewGroup userCardLayout;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private ImageView checkbox;
    private UserClickListener userClickListener;

    public UserPickerCard(View view, UserClickListener userClickListener) {
        catalogText = (TextView) view.findViewById(R.id.text_catalog);
        userCardLayout = (ViewGroup) view.findViewById(R.id.layout_user_card);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        checkbox = (ImageView) view.findViewById(R.id.checkbox);
        this.userClickListener = userClickListener;
    }

    public void refresh(final UserInfo user) {
        avatarImage.getFromUrl(user.getAvatarUrl());
        screenNameText.setText(user.getScreenName());
        checkbox.setSelected(userClickListener.isSelected(user.getObjectId()));

        userCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userClickListener != null) {
                    if (userClickListener.onUserClick(user.getObjectId())) {
                        checkbox.setSelected(!checkbox.isSelected());
                        Animation.scaleIn(checkbox, Animation.Duration.SHORT);
                    }
                }
            }
        });
    }

    public void refreshCatalog(String catalog) {
        if (catalog != null) {
            catalogText.setText(catalog);
            catalogText.setVisibility(View.VISIBLE);
        } else {
            catalogText.setVisibility(View.GONE);
        }
    }
}
