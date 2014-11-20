package com.aumum.app.mobile.ui.contact;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;

import java.util.List;

/**
 * Created by Administrator on 20/11/2014.
 */
public class ContactRequestAdapter extends ArrayAdapter<ContactRequest> {

    private Activity activity;
    private List<ContactRequest> dataSet;

    public ContactRequestAdapter(Activity activity, List<ContactRequest> objects) {
        super(activity, 0, objects);
        this.activity = activity;
        this.dataSet = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContactRequestCard card;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_request_listitem_inner, parent, false);
            card = new ContactRequestCard(activity, convertView);
            convertView.setTag(card);
        } else {
            card = (ContactRequestCard) convertView.getTag();
        }

        ContactRequest request = dataSet.get(position);
        card.refresh(request);

        return convertView;
    }
}
