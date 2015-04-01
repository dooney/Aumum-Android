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
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 1/04/2015.
 */
public class RecommendAskingGroupFragment extends ItemListFragment<AskingGroup>
        implements AskingGroupQuitListener {

    @Inject UserStore userStore;
    @Inject RestService restService;

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
    protected ArrayAdapter<AskingGroup> createAdapter(List<AskingGroup> items) {
        return new AskingGroupAdapter(getActivity(), items, this);
    }

    @Override
    protected List<AskingGroup> loadDataCore(Bundle bundle) throws Exception {
        User currentUser = userStore.getCurrentUser();
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add(currentUser.getCity());
        for (String tag: currentUser.getTags()) {
            keywords.add(tag);
        }
        return restService.getAskingGroupListByKeywords(keywords);
    }

    @Override
    public void onQuit(AskingGroup askingGroup) {

    }
}
