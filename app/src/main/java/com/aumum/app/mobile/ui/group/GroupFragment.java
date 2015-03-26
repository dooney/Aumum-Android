package com.aumum.app.mobile.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.GroupDetails;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.events.RefreshGroupEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.sort.SizeComparator;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GroupFragment extends ItemListFragment<GroupDetails> {

    @Inject Bus bus;
    @Inject UserStore userStore;
    @Inject ChatService chatService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    protected ArrayAdapter<GroupDetails> createAdapter(List<GroupDetails> items) {
        return new GroupsAdapter(getActivity(), items);
    }

    @Override
    protected List<GroupDetails> loadDataCore(Bundle bundle) throws Exception {
        List<EMGroupInfo> groupInfoList = chatService.getPublicGroups();
        ArrayList<EMGroup> groups = new ArrayList<>();
        for (EMGroupInfo groupInfo: groupInfoList) {
            EMGroup group = chatService.getGroupById(groupInfo.getGroupId());
            groups.add(group);
        }
        ArrayList<GroupDetails> groupList = new ArrayList<>();
        User currentUser = userStore.getCurrentUser();
        for (EMGroup group: groups) {
            User user = userStore.getUserByChatId(group.getOwner());
            if (user != null) {
                int groupSize = group.getMembers().size();
                boolean isMember = group.getMembers().contains(currentUser.getChatId());
                GroupDetails groupDetails = new GroupDetails(group.getGroupId(),
                        group.getGroupName(), user, groupSize, isMember);
                groupList.add(groupDetails);
            }
        }
        Collections.sort(groupList, new SizeComparator());
        return groupList;
    }

    @Subscribe
    public void onRefreshGroupEvent(RefreshGroupEvent event) {
        getMainView().setVisibility(View.GONE);
        reload();
    }
}
