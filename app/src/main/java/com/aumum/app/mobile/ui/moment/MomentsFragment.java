package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.dao.vm.UserVM;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.ui.base.CardListFragment;
import com.aumum.app.mobile.ui.party.PartiesActivity;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MomentsFragment extends CardListFragment {

    @Inject UserStore userStore;

    protected List<Moment> dataSet = new ArrayList<Moment>();
    protected MomentStore dataStore;
    protected MenuItem checkInButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
        dataStore = new MomentStore();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_moments);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_check_in))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        checkInButton = menu.findItem(0);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        final Intent intent = new Intent(getActivity(), PartiesActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_moments;
    }

    @Override
    protected List<Card> loadCards(int mode) throws Exception {
        List<Moment> momentList;
        UserVM currentUser = userStore.getCurrentUser(false);
        switch (mode) {
            case UPWARDS_REFRESH:
                momentList = getUpwardsList(currentUser.getMomentList());
                break;
            case BACKWARDS_REFRESH:
                momentList = getBackwardsList(currentUser.getMomentList());
                break;
            default:
                throw new Exception("Invalid refresh mode: " + mode);
        }
        if (momentList != null) {
            for (Moment moment : momentList) {
                UserVM user = userStore.getUserById(moment.getUserId(), false);
                moment.setUser(user);
            }
        }
        return buildCards();
    }

    private List<Moment> getUpwardsList(List<String> idList) {
        dataStore.refresh(dataSet);
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getCreatedAt();
        }
        List<Moment> momentList = dataStore.getUpwardsList(idList, after);
        Collections.reverse(momentList);
        for(Moment moment: momentList) {
            dataSet.add(0, moment);
        }
        return momentList;
    }

    private List<Moment> getBackwardsList(List<String> idList) {
        if (dataSet.size() > 0) {
            Moment last = dataSet.get(dataSet.size() - 1);
            List<Moment> momentList = dataStore.getBackwardsList(idList, last.getCreatedAt());
            dataSet.addAll(momentList);
            if (momentList.size() > 0) {
                setLoadMore(true);
            } else {
                setLoadMore(false);
                Toaster.showShort(getActivity(), R.string.info_all_loaded);
            }
            return momentList;
        }
        return null;
    }

    private List<Card> buildCards() throws Exception {
        List<Card> cards = new ArrayList<Card>();
        if (dataSet.size() > 0) {
            UserVM user = userStore.getCurrentUser(false);
            for (Moment moment : dataSet) {
                Card card = new MomentCard(getActivity(), moment, user.getObjectId());
                cards.add(card);
            }
        }
        return cards;
    }
}
