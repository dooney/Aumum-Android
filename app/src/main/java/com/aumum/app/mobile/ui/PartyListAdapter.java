package com.aumum.app.mobile.ui;

import android.view.LayoutInflater;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Party;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import java.util.List;

/**
 * Created by Administrator on 27/09/2014.
 */
public class PartyListAdapter extends SingleTypeAdapter<Party> {
    /**
     * @param inflater
     * @param items
     */
    public PartyListAdapter(final LayoutInflater inflater, final List<Party> items) {
        super(inflater, R.layout.party_list_item);

        setItems(items);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[0];
    }

    @Override
    protected void update(int i, Party party) {

    }
}
