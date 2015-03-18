package com.aumum.app.mobile.ui.feed.channel;

import android.app.Activity;
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
import com.aumum.app.mobile.core.service.ShareService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.moment.ShareMomentActivity;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 15/03/2015.
 */
public class ChannelFragment extends ItemListFragment<Card> {

    @Inject ChannelItemStore channelItemStore;
    private ShareService shareService;

    private String uri;

    private final int INTENT_SHARE_MOMENT = 100;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        uri = intent.getStringExtra(ChannelActivity.INTENT_URI);

        shareService = new ShareService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_channel, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_SHARE_MOMENT && resultCode == Activity.RESULT_OK) {
            Toaster.showShort(getActivity(), R.string.info_moment_shared);
        }
    }

    @Override
    protected ArrayAdapter<Card> createAdapter(List<Card> items) {
        return new CardArrayAdapter(getActivity(), items);
    }

    @Override
    protected List<Card> loadDataCore(Bundle bundle) throws Exception {
        List<ChannelItem> channelItems = channelItemStore.getUpwardsList(uri);
        List<Card> cards = new ArrayList<Card>();
        for (final ChannelItem item : channelItems) {
            Card card = new ChannelItemCard(getActivity(), item);
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    showActions(item);
                }
            });
            cards.add(card);
        }
        return cards;
    }

    private void showActions(final ChannelItem item) {
        final String options[] = getResources().getStringArray(R.array.label_channel_item_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startShareMomentActivity(item);
                                break;
                            case 1:
                                showShare(item);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startShareMomentActivity(ChannelItem item) {
        final Intent intent = new Intent(getActivity(), ShareMomentActivity.class);
        intent.putExtra(ShareMomentActivity.INTENT_MOMENT_TEXT, item.getText());
        intent.putStringArrayListExtra(ShareMomentActivity.INTENT_MOMENT_IMAGES, item.getImages());
        startActivityForResult(intent, INTENT_SHARE_MOMENT);
    }

    private void showShare(ChannelItem item) {
        String imageUrl = null;
        if (item.getImages().size() > 0) {
            imageUrl = item.getImages().get(0);
        }
        shareService.show(item.getText(), null, imageUrl);
    }
}
