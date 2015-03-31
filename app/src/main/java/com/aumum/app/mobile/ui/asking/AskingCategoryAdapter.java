package com.aumum.app.mobile.ui.asking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingCategory;

import java.util.List;

/**
 * Created by Administrator on 31/03/2015.
 */
public class AskingCategoryAdapter extends ArrayAdapter<AskingCategory> {

    public AskingCategoryAdapter(Context context,
                                 List<AskingCategory> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AskingCategoryCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.asking_category_listitem_inner, parent, false);
            card = new AskingCategoryCard(convertView);
            convertView.setTag(card);
        } else {
            card = (AskingCategoryCard) convertView.getTag();
        }

        AskingCategory askingCategory = getItem(position);
        card.refresh(askingCategory);

        return convertView;
    }
}
