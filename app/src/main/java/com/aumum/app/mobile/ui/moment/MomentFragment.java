package com.aumum.app.mobile.ui.moment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 24/04/2015.
 */
public class MomentFragment extends RefreshItemListFragment<Moment> {

    @Inject MomentStore momentStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moment, null);
    }

    @Override
    protected ArrayAdapter<Moment> createAdapter(List<Moment> items) {
        return new MomentCardsAdapter(getActivity(), items);
    }

    @Override
    protected List<Moment> refresh(String after) {
        return null;
    }

    @Override
    protected List<Moment> loadMore(String before) {
        return null;
    }
}
