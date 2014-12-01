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
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SearchAskingFragment extends ItemListFragment<Asking> {

    @Inject UserStore userStore;
    @Inject AskingStore askingStore;

    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        userId = intent.getStringExtra(SearchAskingActivity.INTENT_USER_ID);
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
        User user = userStore.getUserById(userId);
        List<Asking> askingList = askingStore.getList(user.getAskings());
        for (Asking asking: askingList) {
            if (asking.getUser() == null) {
                asking.setUser(userStore.getUserById(asking.getUserId()));
            }
        }
        return askingList;
    }
}
