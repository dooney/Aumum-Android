package com.aumum.app.mobile.ui.moment;

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
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.ui.view.ListViewDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 2/03/2015.
 */
public class MomentListFragment extends RefreshItemListFragment<Card>
        implements MomentCommentClickListener {

    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject ApiKeyProvider apiKeyProvider;

    protected List<Moment> dataSet = new ArrayList<Moment>();

    private static int NEW_MOMENT_REQ_CODE = 100;
    private static int GET_MOMENT_DETAILS_REQ_CODE = 101;

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
        return inflater.inflate(R.layout.fragment_moment_list, null);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_MOMENT_REQ_CODE && resultCode == Activity.RESULT_OK) {
            refresh(null);
        }
    }

    @Override
    protected void getUpwardsList() throws Exception {
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getCreatedAt();
        }
        List<Moment> momentList = onGetUpwardsList(after);
        Collections.reverse(momentList);
        for(Moment moment: momentList) {
            dataSet.add(0, moment);
        }
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            Moment last = dataSet.get(dataSet.size() - 1);
            List<Moment> momentList = onGetBackwardsList(last.getCreatedAt());
            dataSet.addAll(momentList);
            if (momentList.size() > 0) {
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
        String currentUserId = apiKeyProvider.getAuthUserId();
        MomentCard card = new MomentCard(getActivity(), moment, currentUserId, this);
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

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    private List<Moment> onGetUpwardsList(String after) throws Exception {
        return momentStore.getUpwardsList(after);
    }

    private List<Moment> onGetBackwardsList(String before) throws Exception {
        return momentStore.getBackwardsList(before);
    }

    private void showActionDialog() {
        final String options[] = getResources().getStringArray(R.array.label_moment_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startNewMomentActivity();
                                break;
                            case 1:
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startNewMomentActivity() {
        final Intent intent = new Intent(getActivity(), NewMomentActivity.class);
        startActivityForResult(intent, NEW_MOMENT_REQ_CODE);
    }

    private void startMomentDetailsActivity(String momentId) {
        /*final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
        intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID, momentId);
        startActivityForResult(intent, GET_MOMENT_DETAILS_REQ_CODE);*/
    }

    @Override
    public void OnClick(String momentId) {
        startMomentDetailsActivity(momentId);
    }
}
