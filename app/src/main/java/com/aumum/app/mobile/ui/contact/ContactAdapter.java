package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserClickListener;

import java.util.List;

/**
 * Created by Administrator on 21/11/2014.
 */
public class ContactAdapter extends ArrayAdapter<User>
        implements SectionIndexer {

    private UserClickListener userClickListener;

    public ContactAdapter(Context context, List<User> objects,
                          UserClickListener userClickListener) {
        super(context, 0, objects);
        this.userClickListener = userClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.contact_listitem_inner, parent, false);
            card = new ContactCard(convertView, userClickListener);
            convertView.setTag(card);
        } else {
            card = (ContactCard) convertView.getTag();
        }

        User user = getItem(position);
        card.refresh(user);

        int section = getSectionForPosition(position);
        if(position == getPositionForSection(section)){
            card.refreshCatalog(user.getSortLetters());
        } else {
            card.refreshCatalog(null);
        }

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = getItem(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return getItem(position).getSortLetters().charAt(0);
    }
}
