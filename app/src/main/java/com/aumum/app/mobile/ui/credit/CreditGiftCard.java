package com.aumum.app.mobile.ui.credit;

import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.CreditGift;
import com.aumum.app.mobile.ui.view.AvatarImageView;

/**
 * Created by Administrator on 28/03/2015.
 */
public class CreditGiftCard {

    private View view;
    private CreditGiftClickListener listener;

    public CreditGiftCard(View view, CreditGiftClickListener listener) {
        this.view = view;
        this.listener = listener;
    }

    public void refresh(final CreditGift creditGift) {
        AvatarImageView avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        avatarImage.getFromUrl(creditGift.getAvatarUrl());

        int cost = Math.abs(creditGift.getCost());
        TextView textView = (TextView) view.findViewById(R.id.text_cost);
        textView.setText(Html.fromHtml(view.getContext()
                .getString(R.string.label_credit_gift_cost, cost)));

        TextView screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        screenNameText.setText(creditGift.getScreenName());

        TextView descriptionText = (TextView) view.findViewById(R.id.text_description);
        descriptionText.setText(creditGift.getDescription());

        Button purchaseButton = (Button) view.findViewById(R.id.b_purchase);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClick(creditGift);
                }
            }
        });
    }
}
