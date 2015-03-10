package com.aumum.app.mobile.ui.special;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemGridFragment;
import com.etsy.android.grid.StaggeredGridView;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 10/03/2015.
 */
public class SpecialProductsFragment extends ItemGridFragment<SpecialProduct> {

    @Inject RestService restService;

    private String specialId;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        specialId = intent.getStringExtra(SpecialDetailsActivity.INTENT_SPECIAL_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_special_products, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaggeredGridView gridView = getGridView();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
    }

    @Override
    protected ArrayAdapter<SpecialProduct> createAdapter(List<SpecialProduct> items) {
        return new SpecialProductsAdapter(getActivity(), items);
    }

    @Override
    protected List<SpecialProduct> loadDataCore(Bundle bundle) throws Exception {
        return restService.getSpecialProductList(specialId);
    }
}
