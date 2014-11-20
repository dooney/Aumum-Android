package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;

import java.util.List;

/**
 * Created by Administrator on 21/11/2014.
 */
public class ContactAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> dataSet;
    private ContactClickListener contactClickListener;

    public ContactAdapter(Context context, List<User> objects, ContactClickListener contactClickListener) {
        super(context, 0, objects);
        this.context = context;
        this.dataSet = objects;
        this.contactClickListener = contactClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactCard card;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_listitem_inner, parent, false);
            card = new ContactCard(convertView, contactClickListener);
            convertView.setTag(card);
        } else {
            card = (ContactCard) convertView.getTag();
        }

        User user = dataSet.get(position);
        card.refresh(user);

        return convertView;
    }
}
