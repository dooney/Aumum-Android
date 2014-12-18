package com.aumum.app.mobile.ui.contact;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.user.UserActivity;
import com.aumum.app.mobile.utils.DialogUtils;
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
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            getData().clear();
            getData().addAll(userStore.getContacts());
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
    protected ArrayAdapter<User> createAdapter(List<User> items) {
        return new ContactAdapter(getActivity(), items, this);
    }

    @Override
    public void onContactClick(String contactId) {
        final Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra(UserActivity.INTENT_USER_ID, contactId);
        startActivity(intent);
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_contact_actions);
        DialogUtils.showDialog(getActivity(), options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                startMyGroupsActivity();
                                break;
                            case 1:
                                startAllGroupsActivity();
                                break;
                            case 2:
                                startContactRequestActivity();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void startMyGroupsActivity() {
        final Intent intent = new Intent(getActivity(), MyGroupsActivity.class);
        startActivity(intent);
    }

    private void startAllGroupsActivity() {
        final Intent intent = new Intent(getActivity(), AllGroupsActivity.class);
        startActivity(intent);
    }

    private void startContactRequestActivity() {
        final Intent intent = new Intent(getActivity(), ContactRequestsActivity.class);
        startActivity(intent);
    }
}
