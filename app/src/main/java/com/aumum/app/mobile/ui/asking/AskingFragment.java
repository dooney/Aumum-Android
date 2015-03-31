package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.AskingCategory;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.NewAskingUnreadEvent;
import com.aumum.app.mobile.events.ResetAskingUnreadEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingFragment extends ItemListFragment<AskingCategory> {

    @Inject ApiKeyProvider apiKeyProvider;
    @Inject RestService restService;
    @Inject Bus bus;

    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem more = menu.add(Menu.NONE, 0, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    showActionDialog();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                startAskingListActivity(position);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.GET_ASKING_LIST_REQ_CODE) {
            AskingCategory askingCategory = getData().get(position);
            askingCategory.setUnread(false);
            getListAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);

        boolean allRead = true;
        List<AskingCategory> askingCategoryList = getData();
        for (AskingCategory askingCategory: askingCategoryList) {
            if (askingCategory.isUnread()) {
                allRead = false;
                break;
            }
        }
        if (allRead) {
            bus.post(new ResetAskingUnreadEvent());
        }
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    protected ArrayAdapter<AskingCategory> createAdapter(List<AskingCategory> items) {
        return new AskingCategoryAdapter(getActivity(), items);
    }

    @Override
    protected List<AskingCategory> loadDataCore(Bundle bundle) throws Exception {
        return restService.getAskingCategoryList();
    }

    private void showActionDialog() {
        final String options[] = getResources().getStringArray(R.array.label_asking_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                startMyAskingListActivity();
                                break;
                            case 1:
                                startMyFavoritesActivity();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void startMyAskingListActivity() {
        String currentUserId = apiKeyProvider.getAuthUserId();
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE, getString(R.string.label_my_askings));
        intent.putExtra(SearchAskingActivity.INTENT_USER_ID, currentUserId);
        startActivity(intent);
    }

    private void startMyFavoritesActivity() {
        String currentUserId = apiKeyProvider.getAuthUserId();
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE, getString(R.string.label_favorite_askings));
        intent.putExtra(SearchAskingActivity.INTENT_USER_ID, currentUserId);
        intent.putExtra(SearchAskingActivity.INTENT_IS_FAVORITE, true);
        startActivity(intent);
    }

    private void startAskingListActivity(int index) {
        AskingCategory askingCategory = getData().get(index);
        final Intent intent = new Intent(getActivity(), AskingListActivity.class);
        intent.putExtra(AskingListActivity.INTENT_TITLE, askingCategory.getScreenName());
        intent.putExtra(AskingListActivity.INTENT_CATEGORY, askingCategory.getCategory());
        startActivityForResult(intent, Constants.RequestCode.GET_ASKING_LIST_REQ_CODE);
    }

    @Subscribe
    public void onNewAskingUnreadEvent(NewAskingUnreadEvent event) {
        List<Integer> categories = event.getCategories();
        List<AskingCategory> askingCategoryList = getData();
        for(Integer category: categories) {
            for (AskingCategory askingCategory: askingCategoryList) {
                if (category.equals(askingCategory.getCategory())) {
                    askingCategory.setUnread(true);
                }
            }
        }
        getListAdapter().notifyDataSetChanged();
    }
}