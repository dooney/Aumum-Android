package com.aumum.app.mobile.ui.party;

import android.os.Bundle;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;

/**
 * Created by Administrator on 27/12/2014.
 */
public class PartyDetailsSingleActivity extends ProgressDialogActivity {

    public static final String INTENT_PARTY_ID = "partyId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        progress.setMessageId(R.string.info_deleting_party);
    }
}
