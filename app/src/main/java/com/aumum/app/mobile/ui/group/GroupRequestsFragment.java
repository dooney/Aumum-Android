package com.aumum.app.mobile.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.GroupRequest;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.easemob.chat.EMGroup;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 25/03/2015.
 */
public class GroupRequestsFragment extends ItemListFragment<GroupRequest> {

    @Inject ChatService chatService;
    @Inject UserStore userStore;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_requests, null);
    }

    @Override
    protected ArrayAdapter<GroupRequest> createAdapter(List<GroupRequest> items) {
        return new GroupRequestsAdapter(getActivity(), items);
    }

    @Override
    protected List<GroupRequest> loadDataCore(Bundle bundle) throws Exception {
        List<GroupRequest> groupRequestList = userStore.getGroupRequestList();
        for (GroupRequest groupRequest : groupRequestList) {
            EMGroup group = chatService.getGroupById(groupRequest.getGroupId());
            groupRequest.setGroupName(group.getGroupName());
            UserInfo user = userStore.getUserInfoByChatId(groupRequest.getUserId());
            groupRequest.setUser(user);
        }
        return groupRequestList;
    }
}
