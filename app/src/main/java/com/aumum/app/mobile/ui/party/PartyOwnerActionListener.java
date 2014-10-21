package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.ui.view.DropdownImageView;

/**
 * Created by Administrator on 21/10/2014.
 */
public class PartyOwnerActionListener implements DropdownImageView.OnDropdownItemClickListener {
    private final String items[] = {"分享", "删除"};

    @Override
    public void onDropdownItemClick(int item) {

    }

    @Override
    public String[] getItems() {
        return items;
    }
}
