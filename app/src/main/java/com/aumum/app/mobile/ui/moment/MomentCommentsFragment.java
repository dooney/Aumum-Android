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
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.model.UserInfo;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 18/05/2015.
 */
public class MomentCommentsFragment extends RefreshItemListFragment<MomentComment> {

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
        return inflater.inflate(R.layout.fragment_moment_comments, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setPullRefreshEnable(false);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MomentComment momentComment = (MomentComment) adapterView.getAdapter().getItem(i);
                final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID,
                        momentComment.getMoment().getObjectId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected ArrayAdapter<MomentComment> createAdapter(List<MomentComment> items) {
        return new MomentCommentsAdapter(getActivity(), items);
    }

    @Override
    protected List<MomentComment> refresh(String after) throws Exception {
        List<MomentComment> likes = messageStore.getMomentCommentsAfter(after);
        loadInfo(likes);
        return likes;
    }

    @Override
    protected List<MomentComment> loadMore(String before) throws Exception {
        List<MomentComment> likes = messageStore.getMomentCommentsBefore(before);
        loadInfo(likes);
        return likes;
    }

    private void loadInfo(List<MomentComment> comments) throws Exception {
        for (MomentComment comment: comments) {
            Moment moment = momentStore.getById(comment.getMomentId());
            comment.setMoment(moment);
            UserInfo user = userStore.getUserInfoById(comment.getUserId());
            comment.setUser(user);
        }
    }
}
