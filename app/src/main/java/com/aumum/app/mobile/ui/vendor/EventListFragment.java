package com.aumum.app.mobile.ui.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Event;
import com.aumum.app.mobile.core.service.ShareService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.EventFinderUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 13/03/2015.
 */
public class EventListFragment extends ItemListFragment<Card> {

    private ShareService shareService;
    private int category;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shareService = new ShareService(getActivity());

        final Intent intent = getActivity().getIntent();
        category = intent.getIntExtra(EventListActivity.INTENT_CATEGORY, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, null);
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    protected List<Card> loadDataCore(Bundle bundle) throws Exception {
        List<Event> eventList = EventFinderUtils.getList(category);
        List<Card> cards = new ArrayList<Card>();
        for (final Event event : eventList) {
            Card card = new EventCard(getActivity(), event);
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    showActions(event);
                }
            });
            cards.add(card);
        }
        return cards;
    }

    private void showActions(final Event event) {
        final String options[] = getResources().getStringArray(R.array.label_event_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startShareEventActivity(event);
                                break;
                            case 1:
                                shareService.show(event.getName(), null, null);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startShareEventActivity(Event event) {

    }
}
