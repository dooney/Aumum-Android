package com.aumum.app.mobile.ui.feed.channel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.ChannelItemStore;
import com.aumum.app.mobile.core.model.ChannelItem;
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 15/03/2015.
 */
public class ChannelFragment extends ItemListFragment<Card> {

    @Inject
    ChannelItemStore channelItemStore;

    private String uri;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        uri = intent.getStringExtra(ChannelActivity.INTENT_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_channel, null);
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    protected List<Card> loadDataCore(Bundle bundle) throws Exception {
        List<ChannelItem> channelItems = channelItemStore.getUpwardsList(uri);
        List<Card> cards = new ArrayList<Card>();
        for (ChannelItem item : channelItems) {
            Card card = new ChannelItemCard(getActivity(), item);
            cards.add(card);
        }
        return cards;
    }
}
