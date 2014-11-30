package com.aumum.app.mobile.ui.asking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Asking;

import java.util.List;

/**
 * Created by Administrator on 27/11/2014.
 */
public class AskingListAdapter extends ArrayAdapter<Asking> {

    private Context context;
    private List<Asking> dataSet;

    public AskingListAdapter(Context context, List<Asking> objects) {
        super(context, 0, objects);
        this.context = context;
        this.dataSet = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AskingCard card;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.asking_listitem_inner, parent, false);
            card = new AskingCard(context, convertView);
            convertView.setTag(card);
        } else {
            card = (AskingCard) convertView.getTag();
        }

        Asking asking = dataSet.get(position);
        card.refresh(asking);

        return convertView;
    }
}
