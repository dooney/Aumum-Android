package com.aumum.app.mobile.ui.contact;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 21/11/2014.
 */
public class ContactCard {

    private TextView catalogText;
    private ViewGroup contactCardLayout;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private ContactClickListener contactClickListener;

    public ContactCard(View view, ContactClickListener contactClickListener) {
        catalogText = (TextView) view.findViewById(R.id.text_catalog);
        contactCardLayout = (ViewGroup) view.findViewById(R.id.layout_contact_card);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        this.contactClickListener = contactClickListener;
    }

    public void refresh(final User user) {
        avatarImage.getFromUrl(user.getAvatarUrl());
        screenNameText.setText(user.getScreenName());

        contactCardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactClickListener != null) {
                    contactClickListener.onContactClick(user.getObjectId());
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
