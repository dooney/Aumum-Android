package com.aumum.app.mobile.ui.feed.article;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.ArticleItemStore;
import com.aumum.app.mobile.core.model.ArticleItem;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.browser.BrowserActivity;
import com.aumum.app.mobile.ui.feed.channel.ChannelActivity;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 18/03/2015.
 */
public class ArticleFragment extends ItemListFragment<ArticleItem> {

    @Inject ArticleItemStore articleItemStore;

    private String uri;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        uri = intent.getStringExtra(ChannelActivity.INTENT_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArticleItem articleItem = getData().get(i);
                startBrowserActivity(articleItem);
            }
        });
    }

    @Override
    protected ArrayAdapter<ArticleItem> createAdapter(List<ArticleItem> items) {
        return new ArticlesAdapter(getActivity(), items);
    }

    @Override
    protected List<ArticleItem> loadDataCore(Bundle bundle) throws Exception {
        return articleItemStore.getUpwardsList(uri);
    }

    private void startBrowserActivity(ArticleItem articleItem) {
        final Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.INTENT_TITLE, articleItem.getTitle());
        intent.putExtra(BrowserActivity.INTENT_URL, articleItem.getLink());
        startActivity(intent);
    }
}
