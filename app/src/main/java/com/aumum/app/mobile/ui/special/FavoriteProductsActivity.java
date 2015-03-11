package com.aumum.app.mobile.ui.special;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 11/03/2015.
 */
public class FavoriteProductsActivity extends ActionBarActivity {

    public final static String INTENT_SPECIAL_ID = "specialId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite_products);
    }
}
