package com.aumum.app.mobile.ui.credit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.CreditGift;

import java.util.List;

/**
 * Created by Administrator on 28/03/2015.
 */
public class CreditGiftsAdapter extends ArrayAdapter<CreditGift> {

    private CreditGiftClickListener listener;

    public CreditGiftsAdapter(Context context,
                              List<CreditGift> objects,
                              CreditGiftClickListener listener) {
        super(context, 0, objects);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CreditGiftCard card;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.credit_gift_listitem_inner, parent, false);
            card = new CreditGiftCard(convertView, listener);
            convertView.setTag(card);
        } else {
            card = (CreditGiftCard) convertView.getTag();
        }

        CreditGift creditGift = getItem(position);
        card.refresh(creditGift);

        return convertView;
    }
}
