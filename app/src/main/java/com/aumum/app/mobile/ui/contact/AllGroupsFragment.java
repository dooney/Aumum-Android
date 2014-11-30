package com.aumum.app.mobile.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.easemob.chat.EMGroup;

import java.util.List;

import javax.inject.Inject;

public class AllGroupsFragment extends ItemListFragment<EMGroup> {

    @Inject ChatService chatService;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_groups, null);
    }

    @Override
    protected ArrayAdapter<EMGroup> createAdapter(List<EMGroup> items) {
        return new GroupsAdapter(getActivity(), items, chatService.getCurrentUser());
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_conversations;
    }

    @Override
    protected List<EMGroup> loadDataCore(Bundle bundle) throws Exception {
        return chatService.getAllPublicGroups();
    }
}
