package com.aumum.app.mobile.ui.contact;

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
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.user.UserActivity;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.sort.InitialComparator;
import com.aumum.app.mobile.ui.view.sort.SideBar;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ContactFragment extends ItemListFragment<User>
        implements ContactClickListener {

    @Inject UserStore userStore;
    @Inject RestService restService;

    private User currentUser;
    private InitialComparator initialComparator;

    private ContactAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
        initialComparator = new InitialComparator();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem more = menu.add(Menu.NONE, 0, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    showActionDialog();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SideBar sideBar = (SideBar) view.findViewById(R.id.sideBar);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    getListView().setSelection(position);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(null);
    }

    @Override
    protected String getErrorMessage(Exception exception) {
        return getString(R.string.error_load_contacts);
    }

    @Override
    protected List<User> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        return getSortedContacts();
    }

    @Override
    protected ArrayAdapter<User> createAdapter(List<User> items) {
        adapter = new ContactAdapter(getActivity(), items, this);
        return adapter;
    }

    @Override
    public boolean onContactClick(String contactId) {
        final Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra(UserActivity.INTENT_USER_ID, contactId);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean isSelected(String contactId) {
        return true;
    }

    private List<User> getSortedContacts() throws Exception {
        List<User> contacts = userStore.getContacts();
        Collections.sort(contacts, initialComparator);
        return contacts;
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_contact_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 0:
                        showAddContactsDialog();
                        break;
                    case 1:
                        startContactRequestActivity();
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void showAddContactsDialog() {
        String options[] = getResources().getStringArray(R.array.label_add_contacts_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 0:
                        showSearchUserDialog();
                        break;
                    case 1:
                        startAddMobileContactsActivity();
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void showSearchUserDialog() {
        final ArrayList<String> userList = new ArrayList<String>();
        new EditTextDialog(getActivity(), R.layout.dialog_edit_text, R.string.hint_search_user,
                new ConfirmDialog.OnConfirmListener() {
            @Override
            public void call(Object value) throws Exception {
                String name = (String) value;
                String userId = restService.getUserByName(name);
                if (userId != null) {
                    userList.add(userId);
                }
            }

            @Override
            public void onException(String errorMessage) {
                Toaster.showShort(getActivity(), errorMessage);
            }

            @Override
            public void onSuccess(Object value) {
                if (userList.size() > 0) {
                    startUserActivity(userList.get(0));
                } else {
                    Toaster.showShort(getActivity(), R.string.info_no_users_found);
                }
            }
        }).show();
    }

    private void startUserActivity(String userId) {
        final Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra(UserActivity.INTENT_USER_ID, userId);
        startActivity(intent);
    }

    private void startAddMobileContactsActivity() {
        final Intent intent = new Intent(getActivity(), MobileContactsActivity.class);
        intent.putExtra(MobileContactsActivity.INTENT_USER_ID, currentUser.getObjectId());
        startActivity(intent);
    }

    private void startContactRequestActivity() {
        final Intent intent = new Intent(getActivity(), ContactRequestsActivity.class);
        startActivity(intent);
    }
}
