package com.aumum.app.mobile.ui.contact;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 25/12/2014.
 */
public class ContactPickerCard {

    private TextView catalogText;
    private ViewGroup contactCardLayout;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private ImageView checkbox;
    private ContactClickListener contactClickListener;

    public ContactPickerCard(View view, ContactClickListener contactClickListener) {
        catalogText = (TextView) view.findViewById(R.id.text_catalog);
        contactCardLayout = (ViewGroup) view.findViewById(R.id.layout_contact_card);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        checkbox = (ImageView) view.findViewById(R.id.checkbox);
        this.contactClickListener = contactClickListener;
    }

    public void refresh(final User user) {
        avatarImage.getFromUrl(user.getAvatarUrl());
        screenNameText.setText(user.getScreenName());

        contactCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactClickListener != null) {
                    if (contactClickListener.onContactClick(user.getObjectId())) {
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
