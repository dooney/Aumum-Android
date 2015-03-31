package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SearchAskingFragment extends RefreshItemListFragment<Asking> {

    @Inject UserStore userStore;
    @Inject AskingStore askingStore;
    @Inject ApiKeyProvider apiKeyProvider;

    private int mode;
    private String userId;
    private User user;
    private List<Asking> dataSet;

    private final int USER_ASKING_LIST = 0;
    private final int FAVORITE_ASKING_LIST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        userId = intent.getStringExtra(SearchAskingActivity.INTENT_USER_ID);
        if (userId != null) {
            mode = USER_ASKING_LIST;
        }
        if (intent.getBooleanExtra(SearchAskingActivity.INTENT_IS_FAVORITE, false)) {
            mode = FAVORITE_ASKING_LIST;
        }

        dataSet = new ArrayList<Asking>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_asking, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Asking asking = getData().get(position);
                final Intent intent = new Intent(getActivity(), AskingDetailsActivity.class);
                intent.putExtra(AskingDetailsActivity.INTENT_ASKING_ID, asking.getObjectId());
                startActivityForResult(intent, Constants.RequestCode.GET_ASKING_DETAILS_REQ_CODE);
            }
        });
    }

    @Override
    protected ArrayAdapter<Asking> createAdapter(List<Asking> items) {
        return new AskingListAdapter(getActivity(), items);
    }

    @Override
    protected List<Asking> loadDataCore(Bundle bundle) throws Exception {
        user = userStore.getUserById(userId);
        return super.loadDataCore(bundle);
    }

    @Override
    protected void getUpwardsList() throws Exception {
        switch (mode) {
            case USER_ASKING_LIST:
                getUserAskingList();
                break;
            case FAVORITE_ASKING_LIST:
                getUserFavoriteAskingList();
                break;
            default:
                throw new Exception("Invalid mode: " + mode);
        }
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            Asking last = dataSet.get(dataSet.size() - 1);
            switch (mode) {
                case USER_ASKING_LIST:
                    getUserAskingListBefore(last.getUpdatedAt());
                    break;
                case FAVORITE_ASKING_LIST:
                    getUserFavoriteAskingListBefore(last.getUpdatedAt());
                    break;
                default:
                    throw new Exception("Invalid mode: " + mode);
            }
        }
    }

    @Override
    protected List<Asking> buildCards() throws Exception {
        if (dataSet.size() > 0) {
            for (Asking asking : dataSet) {
                if (asking.getUser() == null) {
                    asking.setUser(userStore.getUserById(asking.getUserId()));
                }
            }
        }
        return dataSet;
    }

    private void getUserAskingList() throws Exception {
        String currentUserId = apiKeyProvider.getAuthUserId();
        boolean excludesAnonymous = !currentUserId.equals(userId);
        List<Asking> askingList = askingStore.getList(user.getAskings(), excludesAnonymous);
        dataSet.addAll(askingList);
    }

    private void getUserAskingListBefore(String time) throws Exception {
        getAskingListBefore(user.getAskings(), time);
    }

    private void getUserFavoriteAskingList() throws Exception {
        List<Asking> askingList = askingStore.getList(user.getFavAskings(), false);
        dataSet.addAll(askingList);
    }

    private void getUserFavoriteAskingListBefore(String time) throws Exception {
        getAskingListBefore(user.getFavAskings(), time);
    }

    private void getAskingListBefore(List<String> idList, String time) throws Exception {
        List<Asking> askingList = askingStore.getBackwardsList(idList, time);
        if (askingList.size() > 0) {
            dataSet.addAll(askingList);
            setMore(true);
        } else {
            setMore(false);
        }
    }
}
