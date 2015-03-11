package com.aumum.app.mobile.ui.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

/**
 * Created by Administrator on 9/03/2015.
 */
public class ImageViewActivity extends ActionBarActivity {

    public static final String INTENT_IMAGE_URI = "imageUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_image_view);

        final Intent intent = getIntent();
        String imageUri = intent.getStringExtra(INTENT_IMAGE_URI);
        ImageView image = (ImageView) findViewById(R.id.image_body);
        ImageLoaderUtils.displayImage(imageUri, image);
    }
}
