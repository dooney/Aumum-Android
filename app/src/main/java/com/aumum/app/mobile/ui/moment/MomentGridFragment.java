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
import com.aumum.app.mobile.ui.view.paginggrid.PagingGridView;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 20/06/2015.
 */
public class MomentGridFragment extends LoaderFragment<List<Moment>> {

    protected @Inject MomentStore momentStore;
    protected @Inject UserStore userStore;

    private PagingGridView gridView;
    private MomentGridAdapter adapter;

    private int query;
    private SafeAsyncTask<Boolean> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setData(new ArrayList<Moment>());

        final Intent intent = getActivity().getIntent();
        query = intent.getIntExtra(MomentGridActivity.INTENT_QUERY, 0);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = (PagingGridView) view.findViewById(R.id.grid_view);
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
        gridView.setPagingListener(new PagingGridView.Paging() {
            @Override
            public void onLoadMoreItems() {
                if (task != null) {
                    return;
                }
                task = new SafeAsyncTask<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        List<Moment> dataSet = getData();
                        if (dataSet.size() > 0) {
                            Moment last = dataSet.get(dataSet.size() - 1);
                            List<Moment> moments = loadMore(last, query);
                            loadUserInfo(moments);
                            final int count = moments.size();
                            if (count > 0) {
                                dataSet.addAll(moments);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!momentStore.isFullLoad(count)) {
                                            gridView.onFinishLoading(false, null);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        gridView.onFinishLoading(false, null);
                                    }
                                });
                            }
                        }
                        return true;
                    }

                    @Override
                    protected void onException(Exception e) throws RuntimeException {
                        if (!(e instanceof RetrofitError)) {
                            showError(e);
                        }
                    }

                    @Override
                    protected void onFinally() throws RuntimeException {
                        task = null;
                    }
                };
                task.execute();
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
        return gridView;
    }

    @Override
    protected List<Moment> loadDataCore(Bundle bundle) throws Exception {
        List<Moment> momentList = refresh(query);
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
            gridView.setHasMoreItems(momentStore.isFullLoad(result.size()));
            getData().clear();
            getData().addAll(result);
            adapter.notifyDataSetChanged();
        }
    }

    private List<Moment> getMomentsByUserList(List<UserInfo> users,
                                              String before) throws Exception {
        List<String> userIds = new ArrayList<>();
        for (UserInfo user : users) {
            userIds.add(user.getObjectId());
        }
        return momentStore.getListByUsers(userIds, before);
    }

    protected List<Moment> refresh(int query) throws Exception {
        switch (query) {
            case MomentGridActivity.QUERY_LATEST:
                return momentStore.getLatestList(null);
            case MomentGridActivity.QUERY_HOTTEST:
                return momentStore.getHottestList(Integer.MAX_VALUE);
            case MomentGridActivity.QUERY_NEARBY: {
                List<UserInfo> userList = userStore.getLocalNearByList();
                return getMomentsByUserList(userList, null);
            }
            case MomentGridActivity.QUERY_TALENT: {
                List<UserInfo> userList = userStore.getLocalTalentList();
                return getMomentsByUserList(userList, null);
            }
            default:
                return new ArrayList<>();
        }
    }

    protected List<Moment> loadMore(Moment last, int query) throws Exception {
        switch (query) {
            case MomentGridActivity.QUERY_LATEST:
                return momentStore.getLatestList(last.getCreatedAt());
            case MomentGridActivity.QUERY_HOTTEST:
                return momentStore.getHottestList(last.getHot());
            case MomentGridActivity.QUERY_NEARBY: {
                List<UserInfo> userList = userStore.getLocalNearByList();
                return getMomentsByUserList(userList, last.getCreatedAt());
            }
            case MomentGridActivity.QUERY_TALENT: {
                List<UserInfo> userList = userStore.getLocalTalentList();
                return getMomentsByUserList(userList, last.getCreatedAt());
            }
            default:
                return new ArrayList<>();
        }
    }
}
