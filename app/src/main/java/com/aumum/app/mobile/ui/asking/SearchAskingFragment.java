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

    private int mode;
    private String userId;
    private User user;
    private List<Asking> dataSet;

    private final int USER_ASKINGS = 0;
    private final int FAVORITE_ASKINGS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        userId = intent.getStringExtra(SearchAskingActivity.INTENT_USER_ID);
        if (userId != null) {
            mode = USER_ASKINGS;
        }
        boolean isFavorite = intent.getBooleanExtra(SearchAskingActivity.INTENT_IS_FAVORITE, false);
        if (isFavorite) {
            mode = FAVORITE_ASKINGS;
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
                String pages[] = getResources().getStringArray(R.array.label_asking_pages);
                intent.putExtra(AskingDetailsActivity.INTENT_TITLE, pages[asking.getCategory()]);
                startActivityForResult(intent, Constants.RequestCode.GET_ASKING_DETAILS_REQ_CODE);
            }
        });
    }

    @Override
    protected ArrayAdapter<Asking> createAdapter(List<Asking> items) {
        return new AskingListAdapter(getActivity(), items);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_askings;
    }

    @Override
    protected List<Asking> loadDataCore(Bundle bundle) throws Exception {
        user = userStore.getUserById(userId);
        return super.loadDataCore(bundle);
    }

    @Override
    protected void getUpwardsList() throws Exception {
        switch (mode) {
            case USER_ASKINGS:
                getUserAskings();
                break;
            case FAVORITE_ASKINGS:
                getUserFavoriteAskings();
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
                case USER_ASKINGS:
                    getUserAskingsBefore(last.getUpdatedAt());
                    break;
                case FAVORITE_ASKINGS:
                    getUserFavoriteAskingsBefore(last.getUpdatedAt());
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

    private void getUserAskings() throws Exception {
        List<Asking> askingList = askingStore.getList(user.getAskings());
        dataSet.addAll(askingList);
    }

    private void getUserAskingsBefore(String time) throws Exception {
        getAskingsBefore(user.getAskings(), time);
    }

    private void getUserFavoriteAskings() throws Exception {
        List<Asking> askingList = askingStore.getList(user.getFavAskings());
        dataSet.addAll(askingList);
    }

    private void getUserFavoriteAskingsBefore(String time) throws Exception {
        getAskingsBefore(user.getFavAskings(), time);
    }

    private void getAskingsBefore(List<String> idList, String time) throws Exception {
        List<Asking> askingList = askingStore.getBackwardsList(idList, time);
        if (askingList.size() > 0) {
            dataSet.addAll(askingList);
            setMore(true);
        } else {
            setMore(false);
        }
    }
}
