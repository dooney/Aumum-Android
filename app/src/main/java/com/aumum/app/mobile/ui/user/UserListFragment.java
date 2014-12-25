package com.aumum.app.mobile.ui.user;


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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends ItemListFragment<User> {

    @Inject UserStore userStore;

    private List<String> users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        users = new ArrayList<String>();
        final Intent intent = getActivity().getIntent();
        users.addAll(intent.getStringArrayListExtra(UserListActivity.INTENT_USER_LIST));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, null);
    }

    @Override
    protected ArrayAdapter<User> createAdapter(List<User> items) {
        return new UserListAdapter(getActivity(), items);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_user_list;
    }

    @Override
    protected List<User> loadDataCore(Bundle bundle) throws Exception {
        List<User> userList = new ArrayList<User>();
        for (String userId: users) {
            User user = userStore.getUserById(userId);
            userList.add(user);
        }
        return userList;
    }
}
