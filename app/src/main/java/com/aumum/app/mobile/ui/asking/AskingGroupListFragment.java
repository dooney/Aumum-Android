package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.AskingGroupStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.AskingGroup;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 2/04/2015.
 */
public class AskingGroupListFragment extends ItemListFragment<AskingGroup> {

    @Inject UserStore userStore;
    @Inject AskingGroupStore askingGroupStore;

    private String boardId;
    private ArrayList<String> groups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        boardId = intent.getStringExtra(AskingGroupListActivity.INTENT_BOARD_ID);
        groups = intent.getStringArrayListExtra(AskingGroupListActivity.INTENT_GROUP_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking_group_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startAskingListActivity(i);
            }
        });
    }

    @Override
    protected ArrayAdapter<AskingGroup> createAdapter(List<AskingGroup> items) {
        return new AskingRecommendGroupAdapter(getActivity(), items, null);
    }

    @Override
    protected List<AskingGroup> loadDataCore(Bundle bundle) throws Exception {
        List<AskingGroup> askingGroupList = new ArrayList<>();
        User currentUser = userStore.getCurrentUser();
        if (boardId != null) {
            askingGroupList = askingGroupStore.getListByBoardId(boardId);
        } else if (groups != null) {
            askingGroupList = askingGroupStore.getList(groups);
        }
        for (AskingGroup askingGroup: askingGroupList) {
            if (currentUser.getAskingGroups().contains(askingGroup.getObjectId())) {
                askingGroup.setMember(true);
            }
        }
        return askingGroupList;
    }

    private void startAskingListActivity(int index) {
        AskingGroup askingGroup = getData().get(index);
        final Intent intent = new Intent(getActivity(), AskingListActivity.class);
        intent.putExtra(AskingListActivity.INTENT_TITLE, askingGroup.getScreenName());
        intent.putExtra(AskingListActivity.INTENT_GROUP_ID, askingGroup.getObjectId());
        startActivity(intent);
    }
}
