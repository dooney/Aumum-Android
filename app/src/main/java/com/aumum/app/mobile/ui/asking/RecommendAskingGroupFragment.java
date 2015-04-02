package com.aumum.app.mobile.ui.asking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.AskingGroup;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.RefreshMyAskingGroupsEvent;
import com.aumum.app.mobile.events.RefreshRecommendAskingGroupsEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 1/04/2015.
 */
public class RecommendAskingGroupFragment extends ItemListFragment<AskingGroup>
        implements AskingRecommendGroupJoinListener {

    @Inject UserStore userStore;
    @Inject RestService restService;
    @Inject Bus bus;

    private View mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend_asking_group, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view.findViewById(R.id.main_view);
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
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected ArrayAdapter<AskingGroup> createAdapter(List<AskingGroup> items) {
        return new AskingRecommendGroupAdapter(getActivity(), items, this);
    }

    @Override
    protected List<AskingGroup> loadDataCore(Bundle bundle) throws Exception {
        User currentUser = userStore.getCurrentUser();
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add(currentUser.getCity());
        for (String tag: currentUser.getTags()) {
            keywords.add(tag);
        }
        return restService.getRecommendAskingGroupList(keywords,
                currentUser.getAskingGroups());
    }

    @Override
    public void onSuccess() {
        bus.post(new RefreshMyAskingGroupsEvent());
        refresh(null);
    }

    @Subscribe
    public void onRefreshRecommendAskingGroupsEvent(RefreshRecommendAskingGroupsEvent event) {
        refresh(null);
    }
}
