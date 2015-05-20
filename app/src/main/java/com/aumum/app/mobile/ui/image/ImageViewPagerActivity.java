package com.aumum.app.mobile.ui.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.BaseActionBarActivity;
import com.aumum.app.mobile.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15/03/2015.
 */
public class ImageViewPagerActivity extends BaseActionBarActivity {

    public static final String INTENT_CURRENT_INDEX = "currentIndex";
    public static final String INTENT_IMAGES = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_image_view_pager);

        final Intent intent = getIntent();
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> views = new ArrayList<View>();
        ArrayList<String> images = intent.getStringArrayListExtra(ImageViewPagerActivity.INTENT_IMAGES);
        for (int i = 0; i < images.size(); i++) {
            View view = inflater.inflate(R.layout.view_image_pager, null);
            ImageView image = (ImageView) view.findViewById(R.id.image_body);
            ImageLoaderUtils.displayImage(images.get(i), image);
            views.add(view);
        }

        PagerAdapter adapter = new ImageViewPagerAdapter(views);
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        int currentIndex = intent.getIntExtra(ImageViewPagerActivity.INTENT_CURRENT_INDEX, 0);
        pager.setCurrentItem(currentIndex);
    }
}