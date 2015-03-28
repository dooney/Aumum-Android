package com.aumum.app.mobile.ui.credit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.aumum.app.mobile.R;

/**
 * Created by Administrator on 28/03/2015.
 */
public class CreditPurchaseActivity extends ActionBarActivity {

    public static final String INTENT_CURRENT_CREDIT = "currentCredit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_credit_purchase);
    }
}
