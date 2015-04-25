package com.aumum.app.mobile.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.contact.AddContactListener;

import java.util.List;

/**
 * Created by Administrator on 15/01/2015.
 */
public class AreaUsersAdapter extends ArrayAdapter<UserInfo> {

    private Context context;
    private User currentUser;
    private AddContactListener addContactListener;

    public AreaUsersAdapter(Context context,
                            List<UserInfo> objects,
                            AddContactListener addContactListener) {
        super(context, 0, objects);
        this.context = context;
        this.addContactListener = addContactListener;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserContactCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_contact_listitem_inner, parent, false);
            card = new UserContactCard(context, convertView, addContactListener);
            convertView.setTag(card);
        } else {
            card = (UserContactCard) convertView.getTag();
        }

        UserInfo user = getItem(position);
        card.refresh(user, currentUser.isContact(user.getObjectId()));

        return convertView;
    }
}
