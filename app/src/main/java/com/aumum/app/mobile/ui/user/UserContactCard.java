package com.aumum.app.mobile.ui.user;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.contact.AddContactListener;

/**
 * Created by Administrator on 15/01/2015.
 */
public class UserContactCard extends UserCard {

    private View view;
    private AddContactListener addContactListener;

    public UserContactCard(Context context,
                           View view,
                           AddContactListener addContactListener) {
        super(context, view);
        this.view = view;
        this.addContactListener = addContactListener;
    }

    public void refresh(final User user, boolean isAdded) {
        super.refresh(user);

        TextView tagsText = (TextView) view.findViewById(R.id.text_tags);
        if (user.getTags().size() > 0) {
            String text = "";
            for(String tag: user.getTags()) {
                text += tag + "  ";
            }
            tagsText.setText(text);
            tagsText.setVisibility(View.VISIBLE);
        } else {
            tagsText.setVisibility(View.GONE);
        }

        TextView inviteButton = (TextView) view.findViewById(R.id.text_invite);
        Button addButton = (Button) view.findViewById(R.id.b_add_contact);
        TextView addedText = (TextView) view.findViewById(R.id.text_added);
        inviteButton.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        addedText.setVisibility(View.GONE);
        if (isAdded) {
            addedText.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addContactListener.onAddContact(user.getObjectId());
                }
            });
        }
    }
}
