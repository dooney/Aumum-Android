package com.aumum.app.mobile.ui.contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;

import java.util.List;

/**
 * Created by Administrator on 25/12/2014.
 */
public class ContactPickerAdapter extends ArrayAdapter<User>
        implements SectionIndexer {

    private Context context;
    private List<User> dataSet;
    private ContactClickListener contactClickListener;

    public ContactPickerAdapter(Context context, List<User> objects,
                                ContactClickListener contactClickListener) {
        super(context, 0, objects);
        this.context = context;
        this.dataSet = objects;
        this.contactClickListener = contactClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactPickerCard card;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_picker_listitem_inner, parent, false);
            card = new ContactPickerCard(convertView, contactClickListener);
            convertView.setTag(card);
        } else {
            card = (ContactPickerCard) convertView.getTag();
        }

        User user = dataSet.get(position);
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
            String sortStr = dataSet.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return dataSet.get(position).getSortLetters().charAt(0);
    }
}
