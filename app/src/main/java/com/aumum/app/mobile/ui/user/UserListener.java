package com.aumum.app.mobile.ui.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by Administrator on 5/10/2014.
 */
public class UserListener implements View.OnClickListener{
    private Context context;
    private String userId;

    public UserListener(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    @Override
    public void onClick(View view) {
        final Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(UserActivity.INTENT_USER_ID, userId);
        context.startActivity(intent);
    }
}
