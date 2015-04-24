package com.aumum.app.mobile.ui.contact;

import android.app.Activity;
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
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.group.MyGroupsActivity;
import com.aumum.app.mobile.ui.user.AreaUsersActivity;
import com.aumum.app.mobile.ui.user.TagUsersActivity;
import com.aumum.app.mobile.ui.user.UserActivity;
import com.aumum.app.mobile.ui.user.UserClickListener;
import com.aumum.app.mobile.ui.user.UserTagListActivity;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.sort.InitialComparator;
import com.aumum.app.mobile.ui.view.sort.SideBar;
import com.aumum.app.mobile.utils.SafeAsyncTask;
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
        implements UserClickListener {

    @Inject UserStore userStore;
    @Inject RestService restService;

    private User currentUser;
    private InitialComparator initialComparator;

    private View mainView;
    private TextView contactsCountText;
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
        MenuItem plus = menu.add(Menu.NONE, 0, Menu.NONE, null);
        plus.setActionView(R.layout.menuitem_plus);
        plus.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View plusView = plus.getActionView();
        ImageView plusIcon = (ImageView) plusView.findViewById(R.id.b_plus);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    showAddContactsDialog();
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

        mainView = view.findViewById(R.id.main_view);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        View headerView = inflater.inflate(R.layout.listview_contact_header, null);
        listView.addHeaderView(headerView, null, false);
        listView.setHeaderDividersEnabled(false);
        View footerView = inflater.inflate(R.layout.listview_contact_footer, null);
        listView.addFooterView(footerView, null, false);
        listView.setFooterDividersEnabled(false);
        contactsCountText = (TextView) footerView.findViewById(R.id.text_count);

        View myGroupsLayout = view.findViewById(R.id.layout_my_groups);
        myGroupsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMyGroupsActivity();
            }
        });
        View contactRequestsLayout = view.findViewById(R.id.layout_contact_requests);
        contactRequestsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startContactRequestActivity();
            }
        });

        SideBar sideBar = (SideBar) view.findViewById(R.id.sideBar);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    getListView().setSelection(position);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.GET_AREA_LIST_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String area = data.getStringExtra(AreaListActivity.INTENT_AREA);
            startAreaUsersActivity(area);
        } else if (requestCode == Constants.RequestCode.GET_USER_TAG_LIST_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            final ArrayList<String> userTags =
                    data.getStringArrayListExtra(UserTagListActivity.INTENT_USER_TAGS);
            if (userTags.size() > 0) {
                startTagUsersActivity(userTags);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(null);
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected boolean readyToShow() {
        return true;
    }

    @Override
    protected ArrayAdapter<User> getListAdapter() {
        HeaderViewListAdapter adapter = (HeaderViewListAdapter)getListView().getAdapter();
        return (ArrayAdapter<User>)adapter.getWrappedAdapter();
    }

    @Override
    protected List<User> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        return getSortedContacts();
    }

    @Override
    protected void handleLoadResult(List<User> result) {
        super.handleLoadResult(result);
        contactsCountText.setText(getString(R.string.label_contact_counts, result.size()));
    }

    @Override
    protected ArrayAdapter<User> createAdapter(List<User> items) {
        adapter = new ContactAdapter(getActivity(), items, this);
        return adapter;
    }

    @Override
    public boolean onUserClick(String userId) {
        final Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra(UserActivity.INTENT_USER_ID, userId);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean isSelected(String userId) {
        return true;
    }

    private List<User> getSortedContacts() throws Exception {
        List<User> contacts = userStore.getContacts();
        Collections.sort(contacts, initialComparator);
        return contacts;
    }

    private void showAddContactsDialog() {
        String options[] = getResources().getStringArray(R.array.label_add_contacts);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                showSearchUserDialog();
                                break;
                            case 1:
                                startUserTagListActivity();
                                break;
                            case 2:
                                startAreaListActivity();
                                break;
                            case 3:
                                startAreaUsersActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void showSearchUserDialog() {
        final ArrayList<String> userList = new ArrayList<String>();
        new SearchUserDialog(getActivity(), new ConfirmDialog.OnConfirmListener() {
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

    private void startMyGroupsActivity() {
        final Intent intent = new Intent(getActivity(), MyGroupsActivity.class);
        startActivity(intent);
    }

    private void startContactRequestActivity() {
        final Intent intent = new Intent(getActivity(), ContactRequestsActivity.class);
        startActivity(intent);
    }

    private void startAreaUsersActivity(String area) {
        final Intent intent = new Intent(getActivity(), AreaUsersActivity.class);
        intent.putExtra(AreaUsersActivity.INTENT_AREA, area);
        startActivity(intent);
    }

    private void startAreaUsersActivity() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                currentUser = userStore.getCurrentUser();
                startAreaUsersActivity(currentUser.getArea());
                return true;
            }
        }.execute();
    }

    private void startAreaListActivity() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                currentUser = userStore.getCurrentUser();
                final Intent intent = new Intent(getActivity(), AreaListActivity.class);
                int cityId = Constants.Options.CITY_ID.get(currentUser.getCity());
                intent.putExtra(AreaListActivity.INTENT_CITY, cityId);
                intent.putExtra(AreaListActivity.INTENT_TITLE, getString(R.string.title_activity_search_area));
                startActivityForResult(intent, Constants.RequestCode.GET_AREA_LIST_REQ_CODE);
                return true;
            }
        }.execute();
    }

    private void startUserTagListActivity() {
        final Intent intent = new Intent(getActivity(), UserTagListActivity.class);
        startActivityForResult(intent, Constants.RequestCode.GET_USER_TAG_LIST_REQ_CODE);
    }

    private void startTagUsersActivity(ArrayList<String> tags) {
        final Intent intent = new Intent(getActivity(), TagUsersActivity.class);
        intent.putStringArrayListExtra(TagUsersActivity.INTENT_TAGS, tags);
        startActivity(intent);
    }
}
