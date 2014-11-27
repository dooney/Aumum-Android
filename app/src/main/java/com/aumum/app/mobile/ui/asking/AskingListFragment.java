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
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.Ln;

import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingListFragment extends ItemListFragment<Asking> {

    @Inject AskingStore askingStore;

    private int category;

    public static final String CATEGORY = "category";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        Bundle bundle = getArguments();
        category = bundle.getInt(CATEGORY);
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
        return askingStore.getUpwardsList(category, null);
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
}
