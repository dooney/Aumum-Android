package com.aumum.app.mobile.ui.contact;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.Ln;
import com.easemob.chat.EMGroup;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyGroupsFragment extends ItemListFragment<EMGroup> {

    @Inject ChatService chatService;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_groups, null);
    }

    @Override
    protected ArrayAdapter<EMGroup> createAdapter(List<EMGroup> items) {
        return new GroupsAdapter(getActivity(), items, chatService.getCurrentUser());
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_my_groups;
    }

    @Override
    protected List<EMGroup> loadDataCore(Bundle bundle) throws Exception {
        return chatService.getMyGroups();
    }

    @Override
    protected void handleLoadResult(List<EMGroup> result) {
        try {
            if (result != null) {
                getData().addAll(result);
                getListAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }
}
