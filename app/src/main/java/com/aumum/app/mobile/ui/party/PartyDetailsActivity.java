package com.aumum.app.mobile.ui.party;

import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

public class PartyDetailsActivity extends ProgressDialogActivity {

    private String partyId;

    public static final String INTENT_PARTY_ID = "partyId";
    public static final String INTENT_DELETED = "deleted";
    public static final String INTENT_QUIT = "quit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        partyId = getIntent().getStringExtra(INTENT_PARTY_ID);
        progress.setMessageId(R.string.info_deleting_party);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        intent.putExtra(INTENT_PARTY_ID, partyId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
