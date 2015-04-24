package com.aumum.app.mobile.ui.view.dialog;

import android.content.Context;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.dialog.PopupDialog;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 8/01/2015.
 */
public class ImageViewDialog extends PopupDialog {

    public ImageViewDialog(Context context, String imageUri) {
        super(context, R.layout.dialog_image);
        setCanceledOnTouchOutside(true);
        initView(imageUri);
    }

    private void initView(String imageUri) {
        ImageView image = (ImageView) findViewById(R.id.image_body);
        ImageLoaderUtils.displayImage(imageUri, image);
    }
}
