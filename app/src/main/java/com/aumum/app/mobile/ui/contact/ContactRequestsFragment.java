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
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ContactRequestsFragment extends ItemListFragment<ContactRequest> {

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
    public void onResume() {
        super.onResume();
        messageStore.resetContactRequestsUnread();
    }

    @Override
    protected ArrayAdapter<ContactRequest> createAdapter(List<ContactRequest> items) {
        return new ContactRequestAdapter(getActivity(), items);
    }

    @Override
    protected List<ContactRequest> loadDataCore(Bundle bundle) throws Exception {
        List<ContactRequest> contactRequestList = messageStore.getContactRequestList();
        User currentUser = userStore.getCurrentUser();
        for (ContactRequest request: contactRequestList) {
            UserInfo user = userStore.getUserInfoById(request.getUserId());
            request.setUser(user);
            boolean isAdded = currentUser.isContact(request.getUserId());
            request.setAdded(isAdded);
        }
        return contactRequestList;
    }
}
