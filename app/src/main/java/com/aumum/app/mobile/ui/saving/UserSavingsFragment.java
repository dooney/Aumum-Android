package com.aumum.app.mobile.ui.saving;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.SavingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Saving;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.ui.saving.SavingCard;
import com.aumum.app.mobile.ui.saving.SavingCommentClickListener;
import com.aumum.app.mobile.ui.saving.SavingDetailsActivity;
import com.aumum.app.mobile.ui.saving.UserSavingsActivity;
import com.aumum.app.mobile.utils.Ln;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 12/03/2015.
 */
public class UserSavingsFragment extends RefreshItemListFragment<Card>
        implements SavingCommentClickListener {

    @Inject UserStore userStore;
    @Inject SavingStore savingStore;

    private User user;
    private User currentUser;
    private String userId;
    private List<Saving> dataSet;

    private static int GET_SAVING_DETAILS_REQ_CODE = 100;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        userId = intent.getStringExtra(UserSavingsActivity.INTENT_USER_ID);

        dataSet = new ArrayList<Saving>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_savings, null);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_SAVING_DETAILS_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String savingId = data.getStringExtra(SavingDetailsActivity.INTENT_SAVING_ID);
            if (data.hasExtra(SavingDetailsActivity.INTENT_DELETED)) {
                onSavingDeleted(savingId);
            } else {
                onSavingRefresh(savingId);
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
        List<Saving> savingList = savingStore.getUpwardsList(user.getSavings());
        dataSet.addAll(savingList);
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            Saving last = dataSet.get(dataSet.size() - 1);
            List<Saving> savingList = savingStore.getBackwardsList(user.getSavings(), last.getCreatedAt());
            if (savingList.size() > 0) {
                dataSet.addAll(savingList);
                setMore(true);
            } else {
                setMore(false);
            }
        }
    }

    private Card buildCard(Saving saving) throws Exception {
        if (saving.getUser() == null) {
            saving.setUser(userStore.getUserById(saving.getUserId()));
        }
        SavingCard card = new SavingCard(getActivity(), saving, currentUser.getObjectId(), this);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                SavingCard savingCard = (SavingCard) card;
                startSavingDetailsActivity(savingCard.getSaving().getObjectId());
            }
        });
        return card;
    }

    @Override
    protected List<Card> buildCards() throws Exception {
        int totalCount = dataSet.size();
        if (totalCount < SavingStore.LIMIT_PER_LOAD) {
            setMore(false);
        }
        List<Card> cards = new ArrayList<Card>();
        if (totalCount > 0) {
            for (Saving saving : dataSet) {
                Card card = buildCard(saving);
                cards.add(card);
            }
        }
        return cards;
    }

    private void startSavingDetailsActivity(String savingId) {
        final Intent intent = new Intent(getActivity(), SavingDetailsActivity.class);
        intent.putExtra(SavingDetailsActivity.INTENT_SAVING_ID, savingId);
        startActivityForResult(intent, GET_SAVING_DETAILS_REQ_CODE);
    }

    @Override
    public void OnClick(String savingId) {
        startSavingDetailsActivity(savingId);
    }

    private void onSavingDeleted(String savingId) {
        try {
            List<Card> cardList = getData();
            for (Iterator<Card> it = cardList.iterator(); it.hasNext();) {
                Card card = it.next();
                Saving saving = ((SavingCard) card).getSaving();
                if (saving.getObjectId().equals(savingId)) {
                    dataSet.remove(saving);
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

    private void onSavingRefresh(String savingId) {
        try {
            for (int i = 0; i < dataSet.size(); i++) {
                Saving item = dataSet.get(i);
                if (item.getObjectId().equals(savingId)) {
                    Saving saving = savingStore.getSavingById(savingId);
                    getData().set(i, buildCard(saving));
                    dataSet.set(i, saving);
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
