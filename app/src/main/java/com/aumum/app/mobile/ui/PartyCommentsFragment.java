package com.aumum.app.mobile.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.PartyComment;
import com.aumum.app.mobile.core.PartyCommentStore;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyCommentsFragment extends ItemListFragment<PartyComment> {
    private String partyId;
    private PartyCommentStore partyCommentStore;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        partyCommentStore = new PartyCommentStore();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        final Intent intent = activity.getIntent();
        partyId = intent.getStringExtra(PartyCommentsActivity.INTENT_PARTY_ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_party_comments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_party_comments, null);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_party_comments;
    }

    @Override
    protected List<PartyComment> loadDataCore(Bundle bundle) throws Exception {
        return partyCommentStore.getPartyComments(partyId);
    }

    @Override
    protected void handleLoadResult(List<PartyComment> result) {
        getListAdapter().notifyDataSetChanged();
    }

    @Override
    protected ArrayAdapter<PartyComment> createAdapter(List<PartyComment> items) {
        return new ArrayAdapter<PartyComment>(getActivity(),
                R.layout.party_comments_listitem_inner, items);
    }
}
