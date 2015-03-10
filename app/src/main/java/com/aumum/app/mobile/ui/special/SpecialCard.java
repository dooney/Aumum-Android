package com.aumum.app.mobile.ui.special;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Special;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialCard {

    private Context context;
    private ImageView vendorImage;
    private TextView infoText;
    private TextView likesText;

    public SpecialCard(View view) {
        this.context = view.getContext();
        vendorImage = (ImageView) view.findViewById(R.id.image_vendor_logo);
        infoText = (TextView) view.findViewById(R.id.text_info);
        likesText = (TextView) view.findViewById(R.id.text_likes);
    }

    public void refresh(Special special) {
        ImageLoaderUtils.displayImage(special.getVendorLogoUrl(), vendorImage);
        infoText.setText(special.getInfo());
        likesText.setText(Html.fromHtml(context.getString(R.string.label_special_likes, special.getLikes())));
    }
}
