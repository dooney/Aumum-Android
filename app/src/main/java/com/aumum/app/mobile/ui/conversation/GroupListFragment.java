package com.aumum.app.mobile.ui.conversation;

import android.content.Intent;
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
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.easemob.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 24/03/2015.
 */
public class GroupListFragment extends ItemListFragment<GroupDetails> {

    @Inject UserStore userStore;
    @Inject ChatService chatService;

    private ArrayList<String> groups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        groups = intent.getStringArrayListExtra(GroupListActivity.INTENT_GROUP_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_list, null);
    }

    @Override
    protected ArrayAdapter<GroupDetails> createAdapter(List<GroupDetails> items) {
        return new GroupsAdapter(getActivity(), items);
    }

    @Override
    protected List<GroupDetails> loadDataCore(Bundle bundle) throws Exception {
        ArrayList<GroupDetails> groupList = new ArrayList<>();
        User currentUser = userStore.getCurrentUser();
        for (String groupId: groups) {
            EMGroup group = chatService.getGroupById(groupId);
            User user = userStore.getUserByChatId(group.getOwner());
            int groupSize = group.getMembers().size();
            boolean isAdded = group.getMembers().contains(currentUser.getChatId());
            GroupDetails groupDetails = new GroupDetails(group.getGroupName(),
                    user, groupSize, isAdded);
            groupList.add(groupDetails);
        }
        return groupList;
    }
}
