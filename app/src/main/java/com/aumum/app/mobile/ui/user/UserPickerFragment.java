package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.sort.InitialComparator;
import com.aumum.app.mobile.ui.view.sort.SideBar;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class UserPickerFragment extends ItemListFragment<UserInfo>
        implements UserClickListener {

    protected ArrayList<String> userList;
    protected InitialComparator initialComparator;

    private View mainView;
    private UserPickerAdapter adapter;
    private View confirmButton;

    private final int MAX_COUNT = 10;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userList = new ArrayList<String>();
        initialComparator = new InitialComparator();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_button_ok);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        confirmButton = view.findViewById(R.id.b_ok);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.putStringArrayListExtra("userList", userList);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        updateUIWithValidation();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view.findViewById(R.id.main_view);
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
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected ArrayAdapter<UserInfo> createAdapter(List<UserInfo> items) {
        adapter = new UserPickerAdapter(getActivity(), items, this);
        return adapter;
    }

    @Override
    public boolean onUserClick(String userId) {
        if (userList.contains(userId)) {
            userList.remove(userId);
        } else {
            if (userList.size() >= MAX_COUNT) {
                Toaster.showShort(getActivity(),
                        getString(R.string.error_selection_no_more_than, MAX_COUNT));
                return false;
            }
            userList.add(userId);
        }
        updateUIWithValidation();
        return true;
    }

    @Override
    public boolean isSelected(String userId) {
        return userList.contains(userId);
    }

    private void updateUIWithValidation() {
        final boolean populated = userList.size() > 0;
        if (confirmButton != null) {
            confirmButton.setEnabled(populated);
        }
    }
}
