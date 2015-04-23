package com.aumum.app.mobile.ui.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.GroupDetails;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.sort.SizeComparator;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 26/03/2015.
 */
public class MyGroupsFragment extends ItemListFragment<GroupDetails> {

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
        return inflater.inflate(R.layout.fragment_my_groups, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.NEW_GROUP_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            refresh(null);
        }
    }

    @Override
    protected ArrayAdapter<GroupDetails> createAdapter(List<GroupDetails> items) {
        return new GroupsAdapter(getActivity(), items);
    }

    @Override
    protected List<GroupDetails> loadDataCore(Bundle bundle) throws Exception {
        List<EMGroup> groups = chatService.getAllGroups();
        ArrayList<GroupDetails> groupList = new ArrayList<>();
        User currentUser = userStore.getCurrentUser();
        for (EMGroup group: groups) {
            boolean isMember = group.getMembers().contains(currentUser.getChatId());
            if (isMember) {
                int groupSize = group.getMembers().size();
                GroupDetails groupDetails = new GroupDetails(group.getGroupId(),
                        group.getGroupName(), group.getDescription(), groupSize, true);
                groupList.add(groupDetails);
            }
        }
        Collections.sort(groupList, new SizeComparator());
        return groupList;
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_group_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startNewGroupActivity();
                                break;
                            case 1:
                                showSearchGroupDialog();
                                break;
                            case 2:
                                startGroupRequestsActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void showSearchGroupDialog() {
        new EditTextDialog(getActivity(),
                R.layout.dialog_edit_text,
                R.string.hint_group_name,
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        final String groupName = (String) value;
                        List<EMGroupInfo> groups = chatService.getPublicGroups();
                        final ArrayList<String> groupList = new ArrayList<>();
                        for(EMGroupInfo groupInfo: groups) {
                            if (groupInfo.getGroupName().contains(groupName)) {
                                groupList.add(groupInfo.getGroupId());
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (groupList.size() > 0) {
                                    startGroupListActivity(groupList);
                                } else {
                                    Toaster.showShort(getActivity(), R.string.info_no_group_found);
                                }
                            }
                        });
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                    }
                }).show();
    }

    private void startGroupListActivity(ArrayList<String> groupList) {
        final Intent intent = new Intent(getActivity(), GroupListActivity.class);
        intent.putStringArrayListExtra(GroupListActivity.INTENT_GROUP_LIST, groupList);
        startActivity(intent);
    }

    private void startGroupRequestsActivity() {
        final Intent intent = new Intent(getActivity(), GroupRequestsActivity.class);
        startActivity(intent);
    }

    private void startNewGroupActivity() {
        final Intent intent = new Intent(getActivity(), NewGroupActivity.class);
        startActivityForResult(intent, Constants.RequestCode.NEW_GROUP_REQ_CODE);
    }
}
