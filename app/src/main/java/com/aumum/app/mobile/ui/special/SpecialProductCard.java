package com.aumum.app.mobile.ui.special;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.aumum.app.mobile.ui.image.ImageViewActivity;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialProductCard {

    Activity activity;
    private ImageView previewImage;
    private TextView nameText;
    private TextView wasText;
    private TextView nowText;

    public SpecialProductCard(Activity activity, View view) {
        this.activity = activity;
        previewImage = (ImageView) view.findViewById(R.id.image_preview);
        nameText = (TextView) view.findViewById(R.id.text_name);
        wasText = (TextView) view.findViewById(R.id.text_was);
        nowText = (TextView) view.findViewById(R.id.text_now);
    }

    public void refresh(final SpecialProduct specialProduct) {
        ImageLoaderUtils.displayImage(specialProduct.getPreviewUrl(), previewImage);
        previewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(activity, ImageViewActivity.class);
                intent.putExtra(ImageViewActivity.INTENT_IMAGE_URI, specialProduct.getPreviewUrl());
                activity.startActivity(intent);
            }
        });
        nameText.setText(specialProduct.getName());
        String was = activity.getString(R.string.label_was, specialProduct.getWas());
        wasText.setText(was);
        String now = activity.getString(R.string.label_now, specialProduct.getNow());
        nowText.setText(now);

    }
}
