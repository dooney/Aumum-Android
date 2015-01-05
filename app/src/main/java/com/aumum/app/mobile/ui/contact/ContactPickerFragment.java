package com.aumum.app.mobile.ui.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.sort.InitialComparator;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactPickerFragment extends ItemListFragment<User>
        implements ContactClickListener {

    @Inject UserStore userStore;

    private ArrayList<String> contacts;
    private InitialComparator initialComparator;

    private Button confirmButton;

    private final int MAX_COUNT = 10;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
        contacts = new ArrayList<String>();
        initialComparator = new InitialComparator();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_ok));
        menuItem.setActionView(R.layout.menuitem_button_ok);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        confirmButton = (Button) view.findViewById(R.id.b_ok);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putStringArrayListExtra(ContactPickerActivity.INTENT_SELECTED_CONTACTS, contacts);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        updateUIWithValidation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_picker, null);
    }

    @Override
    protected String getErrorMessage(Exception exception) {
        return getString(R.string.error_load_contacts);
    }

    @Override
    protected List<User> loadDataCore(Bundle bundle) throws Exception {
        return getSortedContacts();
    }

    @Override
    protected ArrayAdapter<User> createAdapter(List<User> items) {
        return new ContactPickerAdapter(getActivity(), items, this);
    }

    private List<User> getSortedContacts() throws Exception {
        List<User> contacts = userStore.getContacts();
        Collections.sort(contacts, initialComparator);
        return contacts;
    }

    @Override
    public boolean onContactClick(String contactId) {
        if (contacts.contains(contactId)) {
            contacts.remove(contactId);
        } else {
            if (contacts.size() >= MAX_COUNT) {
                Toaster.showShort(getActivity(), R.string.error_selection_no_more_than, MAX_COUNT);
                return false;
            }
            contacts.add(contactId);
        }
        updateUIWithValidation();
        return true;
    }

    private void updateUIWithValidation() {
        final boolean populated = contacts.size() > 0;
        confirmButton.setEnabled(populated);
    }
}
