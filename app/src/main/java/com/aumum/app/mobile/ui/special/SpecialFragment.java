package com.aumum.app.mobile.ui.special;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Special;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemGridFragment;
import com.etsy.android.grid.StaggeredGridView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialFragment extends ItemGridFragment<Special> {

    @Inject RestService restService;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_special, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaggeredGridView gridView = getGridView();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Special special = getData().get(i);
                String specialId = special.getObjectId();
                String specialName = special.getName();
                final Intent intent = new Intent(getActivity(), SpecialDetailsActivity.class);
                intent.putExtra(SpecialDetailsActivity.INTENT_SPECIAL_ID, specialId);
                intent.putExtra(SpecialDetailsActivity.INTENT_SPECIAL_NAME, specialName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected ArrayAdapter<Special> createAdapter(List<Special> items) {
        return new SpecialAdapter(getActivity(), items);
    }

    @Override
    protected List<Special> loadDataCore(Bundle bundle) throws Exception {
        return restService.getSpecialList();
    }
}
