package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.ui.view.DropdownImageView;

/**
 * Created by Administrator on 21/10/2014.
 */
public class PartyUserActionListener extends PartyActionListener
        implements DropdownImageView.OnItemClickListener {
    private final String items[] = {"分享"};

    public PartyUserActionListener(Party party) {
        super(party);
    }

    @Override
    public void onItemClick(int item) {
        switch (item) {
            case 0:
                shareParty();
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getItems() {
        return items;
    }
}
