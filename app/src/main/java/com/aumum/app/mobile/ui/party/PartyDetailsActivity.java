package com.aumum.app.mobile.ui.party;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.aumum.app.mobile.R;

public class PartyDetailsActivity extends ActionBarActivity {

    private String partyId;

    public static final String INTENT_PARTY_ID = "partyId";
    public static final String INTENT_DELETED = "deleted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        partyId = getIntent().getStringExtra(INTENT_PARTY_ID);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        intent.putExtra(INTENT_PARTY_ID, partyId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
