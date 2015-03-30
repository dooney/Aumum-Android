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
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.UMengUtils;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 13/03/2015.
 */
public class PartyRequestListFragment extends RefreshItemListFragment<Card>
        implements PartyRequestDeleteListener, PartyRequestMessagingListener {

    @Inject UserStore userStore;
    @Inject PartyRequestStore partyRequestStore;
    @Inject RestService restService;
    @Inject ChatService chatService;

    protected List<PartyRequest> dataSet = new ArrayList<>();
    private ViewGroup container;

    private final String PARTY_REQUEST_EVENT_ID = "party_request_messaging";

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

        if (container.getTag() != null) {
            int requestCode = (Integer) container.getTag();
            if (requestCode == Constants.RequestCode.NEW_PARTY_REQUEST_REQ_CODE) {
                refresh(null);
                container.setTag(null);
            }
        }
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
                User currentUser = userStore.getCurrentUser();
                Card card = new PartyRequestCard(getActivity(), partyRequest,
                        currentUser.getObjectId(), this, this);
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

    @Override
    public void onDelete(final PartyRequest partyRequest) {
        new TextViewDialog(getActivity(),
                getString(R.string.info_confirm_delete_party_request),
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        restService.deletePartyRequest(partyRequest.getObjectId());
                        partyRequestStore.deletePartyRequest(partyRequest.getObjectId());
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        onPartyRequestDeletedSuccess(partyRequest.getObjectId());
                    }
                }).show();
    }

    private void onPartyRequestDeletedSuccess(String partyRequestId) {
        try {
            List<Card> cardList = getData();
            for (Iterator<Card> it = cardList.iterator(); it.hasNext();) {
                Card card = it.next();
                PartyRequest partyRequest = ((PartyRequestCard) card).getPartyRequest();
                if (partyRequest.getObjectId().equals(partyRequestId)) {
                    dataSet.remove(partyRequest);
                    it.remove();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getListAdapter().notifyDataSetChanged();
                        }
                    });
                    Toaster.showShort(getActivity(), R.string.info_party_request_deleted);
                    return;
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    @Override
    public void onMessaging(PartyRequest partyRequest) {
        String text = partyRequest.getDetails();
        chatService.sendSystemMessage(partyRequest.getUser().getChatId(), false, text, null);
        UMengUtils.onEvent(getActivity(), PARTY_REQUEST_EVENT_ID);
    }
}
