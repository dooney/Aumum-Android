package com.aumum.app.mobile.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.FeedItemStore;
import com.aumum.app.mobile.core.model.FeedItem;
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 14/03/2015.
 */
public class FeedFragment extends ItemListFragment<Card> {

    @Inject FeedItemStore feedItemStore;

    private String uri;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        uri = "http://0.smyx.net/sinarss.php?uid=1820069143&item=20&v=1&type=1&key=jyEre5";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, null);
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    protected List<Card> loadDataCore(Bundle bundle) throws Exception {
        List<FeedItem> feedItems = feedItemStore.getUpwardsList(uri);
        List<Card> cards = new ArrayList<Card>();
        for (FeedItem item : feedItems) {
            Card card = new FeedItemCard(getActivity(), item);
            cards.add(card);
        }
        return cards;
    }
}
