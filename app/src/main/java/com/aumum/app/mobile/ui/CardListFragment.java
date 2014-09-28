package com.aumum.app.mobile.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 28/09/2014.
 */
public abstract class CardListFragment extends ItemListFragment<Card> {

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    public Loader<List<Card>> onCreateLoader(int i, Bundle bundle) {
        final List<Card> initialItems = items;
        return new ThrowableLoader<List<Card>>(getActivity(), items) {
            @Override
            public List<Card> loadData() throws Exception {
                try {
                    return loadCards();
                } catch (final OperationCanceledException e) {
                    final Activity activity = getActivity();
                    if (activity != null) {
                        activity.finish();
                    }
                    return initialItems;
                }
            }
        };
    }

    protected abstract List<Card> loadCards() throws Exception;
}
