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
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.user.AreaUsersActivity;
import com.aumum.app.mobile.ui.user.UserActivity;
import com.aumum.app.mobile.ui.user.UserClickListener;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
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
public class ContactFragment extends ItemListFragment<UserInfo>
        implements UserClickListener {

    @Inject UserStore userStore;
    @Inject RestService restService;

    private String city;
    private String area;
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
        }
    }

    @Override
    protected boolean readyToShow() {
        return true;
    }

    @Override
    protected List<UserInfo> loadDataCore(Bundle bundle) throws Exception {
        User currentUser = userStore.getCurrentUser();
        city = currentUser.getCity();
        area = currentUser.getArea();
        return getSortedContacts(currentUser);
    }

    @Override
    protected void handleLoadResult(List<UserInfo> result) {
        super.handleLoadResult(result);
        getActivity().setTitle(getString(R.string.label_my_contacts, result.size()));
    }

    @Override
    protected ArrayAdapter<UserInfo> createAdapter(List<UserInfo> items) {
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

    private List<UserInfo> getSortedContacts(User currentUser) throws Exception {
        List<UserInfo> contacts = userStore.getUserInfoList(currentUser.getContacts());
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
                                startAreaListActivity(city);
                                break;
                            case 2:
                                startAreaUsersActivity(area);
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

    private void startAreaUsersActivity(String area) {
        final Intent intent = new Intent(getActivity(), AreaUsersActivity.class);
        intent.putExtra(AreaUsersActivity.INTENT_AREA, area);
        startActivity(intent);
    }

    private void startAreaListActivity(String city) {
        final Intent intent = new Intent(getActivity(), AreaListActivity.class);
        int cityId = Constants.Options.CITY_ID.get(city);
        intent.putExtra(AreaListActivity.INTENT_CITY, cityId);
        intent.putExtra(AreaListActivity.INTENT_TITLE, getString(R.string.title_activity_search_area));
        startActivityForResult(intent, Constants.RequestCode.GET_AREA_LIST_REQ_CODE);
    }
}
