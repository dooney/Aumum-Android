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
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.AskingGroupStore;
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.AskingGroup;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.NewAskingUnreadEvent;
import com.aumum.app.mobile.events.RefreshMyAskingGroupsEvent;
import com.aumum.app.mobile.events.RefreshRecommendAskingGroupsEvent;
import com.aumum.app.mobile.events.ResetAskingUnreadEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingFragment extends ItemListFragment<AskingGroup>
        implements AskingGroupQuitListener {

    @Inject UserStore userStore;
    @Inject AskingGroupStore askingGroupStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject RestService restService;
    @Inject Bus bus;

    private User currentUser;
    private int position;

    private View scrollView;
    private TextView myGroupsText;
    private TextView moreText;

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

        scrollView = view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        myGroupsText = (TextView) view.findViewById(R.id.text_my_asking_groups);
        moreText = (TextView) view.findViewById(R.id.text_more);
        moreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAskingGroupActivity();
            }
        });
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
            AskingGroup askingGroup = getData().get(position);
            askingGroup.setUnread(false);
            getListAdapter().notifyDataSetChanged();
        } else if (requestCode == Constants.RequestCode.GET_ASKING_BOARD_REQ_CODE) {
            refresh(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);

        boolean allRead = true;
        List<AskingGroup> askingGroupList = getData();
        for (AskingGroup askingGroup : askingGroupList) {
            if (askingGroup.isUnread()) {
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
    public View getMainView() {
        return scrollView;
    }

    @Override
    protected boolean readyToShow() {
        return true;
    }

    @Override
    protected ArrayAdapter<AskingGroup> createAdapter(List<AskingGroup> items) {
        return new AskingGroupAdapter(getActivity(), items, this);
    }

    @Override
    protected List<AskingGroup> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        List<AskingGroup> askingGroupList = askingGroupStore.getList(
                currentUser.getAskingGroups());
        for(AskingGroup askingGroup: askingGroupList) {
            askingGroup.setMember(true);
        }
        return askingGroupList;
    }

    @Override
    protected void handleLoadResult(List<AskingGroup> result) {
        super.handleLoadResult(result);
        myGroupsText.setText(getString(R.string.label_my_asking_groups, result.size()));
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
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE, getString(R.string.label_my_askings));
        intent.putExtra(SearchAskingActivity.INTENT_USER_ID, currentUser.getObjectId());
        startActivity(intent);
    }

    private void startMyFavoritesActivity() {
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE, getString(R.string.label_favorite_askings));
        intent.putExtra(SearchAskingActivity.INTENT_USER_ID, currentUser.getObjectId());
        intent.putExtra(SearchAskingActivity.INTENT_IS_FAVORITE, true);
        startActivity(intent);
    }

    private void startAskingListActivity(int index) {
        AskingGroup askingGroup = getData().get(index);
        final Intent intent = new Intent(getActivity(), AskingListActivity.class);
        intent.putExtra(AskingListActivity.INTENT_TITLE, askingGroup.getScreenName());
        intent.putExtra(AskingListActivity.INTENT_GROUP_ID, askingGroup.getObjectId());
        startActivityForResult(intent, Constants.RequestCode.GET_ASKING_LIST_REQ_CODE);
    }

    @Subscribe
    public void onNewAskingUnreadEvent(NewAskingUnreadEvent event) {
        List<String> groups = event.getGroups();
        List<AskingGroup> askingGroupList = getData();
        for(String groupId: groups) {
            for (AskingGroup askingGroup : askingGroupList) {
                if (groupId.equals(askingGroup.getObjectId())) {
                    askingGroup.setUnread(true);
                }
            }
        }
        getListAdapter().notifyDataSetChanged();
    }

    @Override
    public void onQuit(final AskingGroup askingGroup) {
        new TextViewDialog(getActivity(),
                getString(R.string.info_confirm_quit_asking_group),
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void call(Object value) throws Exception {
                        User currentUser = userStore.getCurrentUser();
                        restService.removeUserAskingGroup(currentUser.getObjectId(),
                                askingGroup.getObjectId());
                        updateCredit(currentUser, CreditRule.REMOVE_ASKING_GROUP);
                        currentUser.removeAskingGroup(askingGroup.getObjectId());
                        userStore.save(currentUser);
                    }

                    @Override
                    public void onException(String errorMessage) {
                        Toaster.showShort(getActivity(), errorMessage);
                    }

                    @Override
                    public void onSuccess(Object value) {
                        bus.post(new RefreshRecommendAskingGroupsEvent());
                        refresh(null);
                    }
                }).show();
    }

    @Subscribe
    public void onRefreshMyAskingGroupsEvent(RefreshMyAskingGroupsEvent event) {
        refresh(null);
    }

    private void startAskingGroupActivity() {
        final Intent intent = new Intent(getActivity(), AskingBoardActivity.class);
        startActivityForResult(intent, Constants.RequestCode.GET_ASKING_BOARD_REQ_CODE);
    }

    private void updateCredit(User currentUser, int seq) {
        CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
        }
    }
}