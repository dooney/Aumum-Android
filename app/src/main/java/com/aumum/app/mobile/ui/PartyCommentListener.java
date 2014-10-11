package com.aumum.app.mobile.ui;

import android.content.Context;
import android.content.Intent;

import com.aumum.app.mobile.ui.view.CommentTextView;

/**
 * Created by Administrator on 10/10/2014.
 */
public class PartyCommentListener implements CommentTextView.OnCommentListener {
    private Context context;
    private String partyId;

    public PartyCommentListener(Context context, String partyId) {
        this.context = context;
        this.partyId = partyId;
    }

    @Override
    public void onComment(CommentTextView view) {
        final Intent intent = new Intent(context, PartyCommentsActivity.class);
        intent.putExtra(PartyCommentsActivity.INTENT_PARTY_ID, partyId);
        context.startActivity(intent);
    }
}
