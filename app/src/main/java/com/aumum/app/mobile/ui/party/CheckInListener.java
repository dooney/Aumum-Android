package com.aumum.app.mobile.ui.party;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.ui.moment.NewMomentActivity;
import com.google.gson.Gson;

/**
 * Created by Administrator on 9/11/2014.
 */
public class CheckInListener implements View.OnClickListener {
    private Context context;
    private Party party;

    public CheckInListener(Context context, Party party) {
        this.context = context;
        this.party = party;
    }

    @Override
    public void onClick(View view) {
        final Intent intent = new Intent(context, NewMomentActivity.class);
        Gson gson = new Gson();
        intent.putExtra(NewMomentActivity.INTENT_PARTY, gson.toJson(party));
        context.startActivity(intent);
    }
}
