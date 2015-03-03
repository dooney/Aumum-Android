package com.aumum.app.mobile.ui.moment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.utils.Ln;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 3/03/2015.
 */
public class UserMomentsFragment extends RefreshItemListFragment<Card>
        implements MomentCommentClickListener {

    @Inject UserStore userStore;
    @Inject MomentStore momentStore;

    private User user;
    private User currentUser;
    private String userId;
    private List<Moment> dataSet;

    private static int GET_MOMENT_DETAILS_REQ_CODE = 100;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        userId = intent.getStringExtra(UserMomentsActivity.INTENT_USER_ID);

        dataSet = new ArrayList<Moment>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_moments, null);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_MOMENT_DETAILS_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String partyId = data.getStringExtra(MomentDetailsActivity.INTENT_MOMENT_ID);
            if (data.hasExtra(MomentDetailsActivity.INTENT_DELETED)) {
                onMomentDeleted(partyId);
            } else {
                onMomentRefresh(partyId);
            }
        }
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    protected List<Card> loadDataCore(Bundle bundle) throws Exception {
        if (userId != null) {
            user = userStore.getUserById(userId);
        }
        currentUser = userStore.getCurrentUser();
        return super.loadDataCore(bundle);
    }

    @Override
    protected void getUpwardsList() throws Exception {
        List<Moment> partyList = momentStore.getUpwardsList(user.getMoments());
        dataSet.addAll(partyList);
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            Moment last = dataSet.get(dataSet.size() - 1);
            List<Moment> momentList = momentStore.getBackwardsList(user.getMoments(), last.getCreatedAt());
            if (momentList.size() > 0) {
                dataSet.addAll(momentList);
                setMore(true);
            } else {
                setMore(false);
            }
        }
    }

    private Card buildCard(Moment moment) throws Exception {
        if (moment.getUser() == null) {
            moment.setUser(userStore.getUserById(moment.getUserId()));
        }
        MomentCard card = new MomentCard(getActivity(), moment, currentUser.getObjectId(), this);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                MomentCard momentCard = (MomentCard) card;
                startMomentDetailsActivity(momentCard.getMoment().getObjectId());
            }
        });
        return card;
    }

    @Override
    protected List<Card> buildCards() throws Exception {
        int totalCount = dataSet.size();
        if (totalCount < MomentStore.LIMIT_PER_LOAD) {
            setMore(false);
        }
        List<Card> cards = new ArrayList<Card>();
        if (totalCount > 0) {
            for (Moment moment : dataSet) {
                Card card = buildCard(moment);
                cards.add(card);
            }
        }
        return cards;
    }

    private void startMomentDetailsActivity(String momentId) {
        final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
        intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID, momentId);
        startActivityForResult(intent, GET_MOMENT_DETAILS_REQ_CODE);
    }

    @Override
    public void OnClick(String momentId) {
        startMomentDetailsActivity(momentId);
    }

    private void onMomentDeleted(String momentId) {
        try {
            List<Card> cardList = getData();
            for (Iterator<Card> it = cardList.iterator(); it.hasNext();) {
                Card card = it.next();
                Moment moment = ((MomentCard) card).getMoment();
                if (moment.getObjectId().equals(momentId)) {
                    dataSet.remove(moment);
                    it.remove();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getListAdapter().notifyDataSetChanged();
                        }
                    });
                    return;
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void onMomentRefresh(String momentId) {
        try {
            for (int i = 0; i < dataSet.size(); i++) {
                Moment item = dataSet.get(i);
                if (item.getObjectId().equals(momentId)) {
                    Moment moment = momentStore.getMomentById(momentId);
                    getData().set(i, buildCard(moment));
                    dataSet.set(i, moment);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getListAdapter().notifyDataSetChanged();
                        }
                    });
                    return;
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }
}
