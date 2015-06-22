package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.view.PagingGridView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 20/06/2015.
 */
public class MomentGridFragment extends LoaderFragment<List<Moment>> {

    @Inject MomentStore momentStore;
    @Inject UserStore userStore;

    private View mainView;
    private MomentGridAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setData(new ArrayList<Moment>());
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view.findViewById(R.id.main_view);

        PagingGridView gridView = (PagingGridView) view.findViewById(R.id.grid_view);
        adapter = new MomentGridAdapter(getActivity(), getData());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Moment moment = adapter.getItem(i);
                final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID, moment.getObjectId());
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moment_grid, null);
    }

    @Override
    protected boolean readyToShow() {
        return !getData().isEmpty();
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected List<Moment> loadDataCore(Bundle bundle) throws Exception {
        List<Moment> momentList = momentStore.refresh(null);
        loadUserInfo(momentList);
        return momentList;
    }

    private void loadUserInfo(List<Moment> momentList) throws Exception {
        User currentUser = userStore.getCurrentUser();
        for (Moment moment: momentList) {
            UserInfo user = userStore.getUserInfoById(moment.getUserId());
            moment.setUser(user);
            moment.setLiked(currentUser.getObjectId());
        }
    }

    @Override
    protected void handleLoadResult(List<Moment> result) {
        if (result != null) {
            getData().clear();
            getData().addAll(result);
            adapter.notifyDataSetChanged();
        }
    }
}
