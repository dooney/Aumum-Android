package com.aumum.app.mobile.ui.asking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.Ln;

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
    private List<Asking> dataSet;

    public static final String CATEGORY = "category";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        Bundle bundle = getArguments();
        category = bundle.getInt(CATEGORY);

        dataSet = new ArrayList<Asking>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking_list, null);
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

    @Override
    protected void handleLoadResult(List<Asking> result) {
        try {
            if (result != null) {
                getData().clear();
                getData().addAll(result);
                getListAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            Ln.d(e);
        }
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
