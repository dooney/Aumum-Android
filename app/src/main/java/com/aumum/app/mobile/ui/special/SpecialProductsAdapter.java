package com.aumum.app.mobile.ui.special;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.SpecialProduct;

import java.util.List;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialProductsAdapter extends ArrayAdapter<SpecialProduct> {

    private Activity activity;

    public SpecialProductsAdapter(Activity activity, List<SpecialProduct> objects) {
        super(activity, 0, objects);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SpecialProductCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.special_product_griditem_inner, parent, false);
            card = new SpecialProductCard(activity, convertView);
            convertView.setTag(card);
        } else {
            card = (SpecialProductCard) convertView.getTag();
        }

        SpecialProduct specialProduct = getItem(position);
        card.refresh(specialProduct);

        return convertView;
    }
}