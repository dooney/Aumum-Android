package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentLike;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 17/05/2015.
 */
public class MomentLikesFragment extends RefreshItemListFragment<MomentLike> {

    @Inject MessageStore messageStore;
    @Inject MomentStore momentStore;
    @Inject UserStore userStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moment_likes, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setPullRefreshEnable(false);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MomentLike momentLike = (MomentLike) adapterView.getAdapter().getItem(i);
                final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID,
                        momentLike.getMoment().getObjectId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected ArrayAdapter<MomentLike> createAdapter(List<MomentLike> items) {
        return new MomentLikesAdapter(getActivity(), items);
    }

    @Override
    protected List<MomentLike> refresh(String after) throws Exception {
        List<MomentLike> likes = messageStore.getMomentLikesAfter(after);
        loadInfo(likes);
        return likes;
    }

    @Override
    protected List<MomentLike> loadMore(String before) throws Exception {
        List<MomentLike> likes = messageStore.getMomentLikesBefore(before);
        loadInfo(likes);
        return likes;
    }

    private void loadInfo(List<MomentLike> likes) throws Exception {
        for (MomentLike like: likes) {
            Moment moment = momentStore.getById(like.getMomentId());
            like.setMoment(moment);
            UserInfo user = userStore.getUserInfoById(like.getUserId());
            like.setUser(user);
        }
    }
}
