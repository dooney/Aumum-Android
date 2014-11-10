package com.aumum.app.mobile.ui.circle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Group;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.Ln;

import java.util.List;

import javax.inject.Inject;

public class SearchCircleFragment extends ItemListFragment<Group> {

    @Inject ApiKeyProvider apiKeyProvider;
    @Inject ChatService chatService;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.error_load_circles);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_circle, null);
    }

    @Override
    protected ArrayAdapter<Group> createAdapter(List<Group> items) {
        return new GroupsAdapter(getActivity(), items);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_circles;
    }

    @Override
    protected List<Group> loadDataCore(Bundle bundle) throws Exception {
        String currentUserId = apiKeyProvider.getAuthUserId();
        return chatService.getAllPublicGroups(currentUserId);
    }

    @Override
    protected void handleLoadResult(List<Group> result) {
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
