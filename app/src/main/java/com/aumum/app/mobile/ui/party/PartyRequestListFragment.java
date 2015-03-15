package com.aumum.app.mobile.ui.party;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.PartyRequestStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.PartyRequest;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyRequestListFragment extends RefreshItemListFragment<Card> {

    @Inject UserStore userStore;
    @Inject PartyRequestStore partyRequestStore;
    @Inject Bus bus;

    protected List<PartyRequest> dataSet = new ArrayList<>();
    private ViewGroup container;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        return inflater.inflate(R.layout.fragment_party_request_list, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);

        if (container.getTag() != null) {
            int requestCode = (Integer) container.getTag();
            if (requestCode == Constants.RequestCode.NEW_PARTY_REQUEST_REQ_CODE) {
                refresh(null);
                container.setTag(null);
            }
        }
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    protected void getUpwardsList() throws Exception {
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getCreatedAt();
        }
        List<PartyRequest> partyRequestList = onGetUpwardsList(after);
        Collections.reverse(partyRequestList);
        for(PartyRequest partyRequest: partyRequestList) {
            dataSet.add(0, partyRequest);
        }
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            PartyRequest last = dataSet.get(dataSet.size() - 1);
            List<PartyRequest> partyRequestList = onGetBackwardsList(last.getCreatedAt());
            dataSet.addAll(partyRequestList);
            if (partyRequestList.size() > 0) {
                setMore(true);
            } else {
                setMore(false);
            }
        }
    }

    @Override
    protected List<Card> buildCards() throws Exception {
        int totalCount = dataSet.size();
        if (totalCount < PartyRequestStore.LIMIT_PER_LOAD) {
            setMore(false);
        }
        List<Card> cards = new ArrayList<Card>();
        if (totalCount > 0) {
            for (PartyRequest partyRequest : dataSet) {
                if (partyRequest.getUser() == null) {
                    partyRequest.setUser(userStore.getUserById(partyRequest.getUserId()));
                }
                Card card = new PartyRequestCard(getActivity(), partyRequest);
                cards.add(card);
            }
        }
        return cards;
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    private List<PartyRequest> onGetUpwardsList(String after) throws Exception {
        return partyRequestStore.getUpwardsList(after);
    }

    private List<PartyRequest> onGetBackwardsList(String before) throws Exception {
        return partyRequestStore.getBackwardsList(before);
    }
}
