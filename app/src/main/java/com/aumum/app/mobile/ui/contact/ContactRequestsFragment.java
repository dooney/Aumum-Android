package com.aumum.app.mobile.ui.contact;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.ContactRequest;
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ContactRequestsFragment extends ItemListFragment<ContactRequest> {

    @Inject UserStore userStore;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_requests, null);
    }

    @Override
    protected ArrayAdapter<ContactRequest> createAdapter(List<ContactRequest> items) {
        return new ContactRequestAdapter(getActivity(), items);
    }

    @Override
    protected List<ContactRequest> loadDataCore(Bundle bundle) throws Exception {
        return userStore.getContactRequestList();
    }
}
