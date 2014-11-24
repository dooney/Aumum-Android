package com.aumum.app.mobile.ui.contact;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InviteFriendsActivity extends Activity {

    @InjectView(R.id.layout_mobile_contacts) protected ViewGroup mobileContactsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        ButterKnife.inject(this);

        mobileContactsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
