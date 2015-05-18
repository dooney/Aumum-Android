package com.aumum.app.mobile.ui.contact;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.ContactRequest;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ContactRequestsFragment extends RefreshItemListFragment<ContactRequest> {

    @Inject UserStore userStore;
    @Inject MessageStore messageStore;

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
    protected List<ContactRequest> refresh(String after) throws Exception {
        List<ContactRequest> requests = messageStore.getContactRequestsAfter(after);
        loadInfo(requests);
        return requests;
    }

    @Override
    protected List<ContactRequest> loadMore(String before) throws Exception {
        List<ContactRequest> requests = messageStore.getContactRequestsBefore(before);
        loadInfo(requests);
        return requests;
    }

    private void loadInfo(List<ContactRequest> contactRequestList) throws Exception {
        User currentUser = userStore.getCurrentUser();
        for (ContactRequest request: contactRequestList) {
            UserInfo user = userStore.getUserInfoById(request.getUserId());
            request.setUser(user);
            boolean isAdded = currentUser.isContact(request.getUserId());
            request.setAdded(isAdded);
        }
    }
}
