package com.aumum.app.mobile.ui;

import android.os.Bundle;

import com.aumum.app.mobile.R;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 3/10/2014.
 */
public class MessageListFragment extends CardListFragment {

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_messages);
    }

    @Override
    protected String getLastItemTime() {
        return null;
    }

    @Override
    protected boolean hasOfflineData() {
        return false;
    }

    @Override
    protected List<Card> loadCards(int mode, String time) throws Exception {
        return new ArrayList<Card>();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_messages;
    }
}
