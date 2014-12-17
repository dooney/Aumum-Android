package com.aumum.app.mobile.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.view.Animation;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InviteFriendsActivity extends ActionBarActivity {

    @InjectView(R.id.layout_mobile_contacts) protected ViewGroup mobileContactsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        ButterKnife.inject(this);

        mobileContactsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(InviteFriendsActivity.this, InviteContactsActivity.class);
                startActivity(intent);
            }
        });

        Animation.flyIn(this);
    }
}
