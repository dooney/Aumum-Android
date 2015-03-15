package com.aumum.app.mobile.ui.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.Ln;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 15/03/2015.
 */
public class GifViewActivity extends ActionBarActivity {

    @InjectView(R.id.image_body) protected GifImageView gifView;

    public static final String INTENT_IMAGE_URI = "imageUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_gif_view);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        String imageUri = intent.getStringExtra(INTENT_IMAGE_URI);
        File gifFile = ImageLoaderUtils.getFile(imageUri);
        try {
            GifDrawable gifDrawable = new GifDrawable(gifFile);
            gifView.setImageDrawable(gifDrawable);
        } catch (Exception e) {
            Ln.e(e);
        }
    }
}
