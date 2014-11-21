package com.aumum.app.mobile.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.user.UserActivity;
import com.aumum.app.mobile.utils.Ln;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ContactFragment extends ItemListFragment<User>
        implements ContactClickListener {

    @Inject UserStore userStore;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.layout_contact_requests);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), ContactRequestsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            getData().clear();
            List<User> contacts = userStore.getContacts();
            getData().addAll(contacts);
            getListAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_contacts;
    }

    @Override
    protected List<User> loadDataCore(Bundle bundle) throws Exception {
        return userStore.getContacts();
    }

    @Override
    protected void handleLoadResult(List<User> result) {
        try {
            if (result != null) {
                getData().clear();
                getData().addAll(result);
                getListAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    @Override
    protected ArrayAdapter<User> createAdapter(List<User> items) {
        return new ContactAdapter(getActivity(), items, this);
    }

    @Override
    public void onContactClick(String contactId) {
        final Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra(UserActivity.INTENT_USER_ID, contactId);
        startActivity(intent);
    }
}
