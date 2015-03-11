package com.aumum.app.mobile.ui.saving;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.SavingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Saving;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.Ln;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingListFragment extends RefreshItemListFragment<Card> 
        implements SavingCommentClickListener {

    @Inject UserStore userStore;
    @Inject SavingStore savingStore;
    @Inject ApiKeyProvider apiKeyProvider;

    protected List<Saving> dataSet = new ArrayList<Saving>();

    private static int NEW_SAVING_REQ_CODE = 100;
    private static int GET_SAVING_DETAILS_REQ_CODE = 101;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem more = menu.add(Menu.NONE, 0, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionDialog();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saving_list, null);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_SAVING_REQ_CODE && resultCode == Activity.RESULT_OK) {
            refresh(null);
        } else if (requestCode == GET_SAVING_DETAILS_REQ_CODE &&
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
    protected void getUpwardsList() throws Exception {
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getCreatedAt();
        }
        List<Saving> savingList = onGetUpwardsList(after);
        Collections.reverse(savingList);
        for(Saving saving: savingList) {
            dataSet.add(0, saving);
        }
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            Saving last = dataSet.get(dataSet.size() - 1);
            List<Saving> savingList = onGetBackwardsList(last.getCreatedAt());
            dataSet.addAll(savingList);
            if (savingList.size() > 0) {
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
        String currentUserId = apiKeyProvider.getAuthUserId();
        SavingCard card = new SavingCard(getActivity(), saving, currentUserId, this);
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

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
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

    private List<Saving> onGetUpwardsList(String after) throws Exception {
        return savingStore.getUpwardsList(after);
    }

    private List<Saving> onGetBackwardsList(String before) throws Exception {
        return savingStore.getBackwardsList(before);
    }

    private void showActionDialog() {
        final String options[] = getResources().getStringArray(R.array.label_saving_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startNewSavingActivity();
                                break;
                            case 1:
                                startMySavingsActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startNewSavingActivity() {
        final Intent intent = new Intent(getActivity(), NewSavingActivity.class);
        startActivityForResult(intent, NEW_SAVING_REQ_CODE);
    }

    private void startMySavingsActivity() {
        final Intent intent = new Intent(getActivity(), UserSavingsActivity.class);
        startActivity(intent);
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
}
