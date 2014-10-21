package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.ui.view.DropdownImageView;

/**
 * Created by Administrator on 21/10/2014.
 */
public class PartyUserActionListener implements DropdownImageView.OnDropdownItemClickListener {
    private final String items[] = {"分享"};

    @Override
    public void onDropdownItemClick(int item) {

    }

    @Override
    public String[] getItems() {
        return items;
    }
}
