package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.os.Bundle;

import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.ui.moment.MomentGridFragment;

import java.util.List;

/**
 * Created by Administrator on 28/06/2015.
 */
public class UserContactsFragment extends MomentGridFragment {

    private List<String> contacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getActivity().getIntent();
        contacts = intent.getStringArrayListExtra(UserContactsActivity.INTENT_CONTACTS);
    }

    @Override
    protected List<Moment> refresh(int query) throws Exception {
        return momentStore.getListByUsers(contacts, null);
    }

    @Override
    protected List<Moment> loadMore(Moment last, int query) throws Exception {
        return momentStore.getListByUsers(contacts, last.getCreatedAt());
    }
}
