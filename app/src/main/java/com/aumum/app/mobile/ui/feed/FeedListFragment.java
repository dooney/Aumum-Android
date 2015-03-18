package com.aumum.app.mobile.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Feed;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.UMengUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 14/03/2015.
 */
public abstract class FeedListFragment extends ItemListFragment<Feed> {

    @Inject RestService restService;

    protected int type;
    protected final int TYPE_CHANNEL = 1;
    protected final int TYPE_ARTICLE = 2;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Feed feed = getData().get(i);
                startFeedActivity(feed);
                updateClickCount(feed.getSeq());
            }
        });
    }

    @Override
    protected ArrayAdapter<Feed> createAdapter(List<Feed> items) {
        return new FeedsAdapter(getActivity(), items);
    }

    @Override
    protected List<Feed> loadDataCore(Bundle bundle) throws Exception {
        return restService.getFeedList(type);
    }

    private void updateClickCount(int id) {
        String eventId = "feed_seq_" + id;
        UMengUtils.onEvent(getActivity(), eventId);
    }

    protected abstract void startFeedActivity(Feed feed);
}
