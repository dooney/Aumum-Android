package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingListFragment extends ItemListFragment<Asking> {

    @Inject AskingStore dataStore;
    @Inject UserStore userStore;

    private int category;
    private String title;
    private List<Asking> dataSet;

    public static final String CATEGORY = "category";
    public static final String TITLE = "title";

    private ViewGroup container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        Bundle bundle = getArguments();
        category = bundle.getInt(CATEGORY);
        title = bundle.getString(TITLE);

        dataSet = new ArrayList<Asking>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        return inflater.inflate(R.layout.fragment_asking_list, null);
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
                intent.putExtra(AskingDetailsActivity.INTENT_TITLE, title);
                startActivityForResult(intent, Constants.RequestCode.GET_ASKING_DETAILS_REQ_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (container.getTag() != null) {
            int category = (Integer) container.getTag();
            if (this.category == category) {
                refresh(null);
                container.setTag(null);
            }
        }
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
        getUpwardsList();
        for (Asking asking: dataSet) {
            if (asking.getUser() == null) {
                asking.setUser(userStore.getUserById(asking.getUserId()));
            }
        }
        return dataSet;
    }

    private void getUpwardsList() throws Exception {
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getCreatedAt();
        }
        List<Asking> askingList = onGetUpwardsList(after);
        Collections.reverse(askingList);
        for(Asking asking: askingList) {
            dataSet.add(0, asking);
        }
    }

    protected List<Asking> onGetUpwardsList(String time) {
        return dataStore.getUpwardsList(category, time);
    }
}
