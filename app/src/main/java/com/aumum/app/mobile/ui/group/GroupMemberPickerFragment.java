package com.aumum.app.mobile.ui.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.user.UserPickerFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GroupMemberPickerFragment extends UserPickerFragment {

    private String allMembers;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getActivity().getIntent();
        allMembers = intent.getStringExtra(GroupMemberPickerActivity.INTENT_ALL_MEMBERS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_member_picker, null);
    }

    @Override
    protected List<User> loadDataCore(Bundle bundle) throws Exception {
        Gson gson = new Gson();
        List<User> userList = gson.fromJson(allMembers, new TypeToken<List<User>>(){}.getType());
        Collections.sort(userList, initialComparator);
        return userList;
    }
}
