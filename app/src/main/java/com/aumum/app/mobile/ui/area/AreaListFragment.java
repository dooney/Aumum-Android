package com.aumum.app.mobile.ui.area;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Area;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.sort.InitialComparator;
import com.aumum.app.mobile.ui.view.sort.SideBar;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 14/01/2015.
 */
public class AreaListFragment extends ItemListFragment<Area>
    implements AreaClickListener{

    @Inject RestService restService;

    private int city;
    private AreaListAdapter adapter;
    private InitialComparator initialComparator;

    private View mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        initialComparator = new InitialComparator();

        final Intent intent = getActivity().getIntent();
        city = intent.getIntExtra(AreaListActivity.INTENT_CITY, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_area_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view.findViewById(R.id.main_view);
        SideBar sideBar = (SideBar) view.findViewById(R.id.sideBar);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    getListView().setSelection(position);
                }
            }
        });
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected ArrayAdapter<Area> createAdapter(List<Area> items) {
        adapter = new AreaListAdapter(getActivity(), items, this);
        return adapter;
    }

    @Override
    protected String getErrorMessage(Exception exception) {
        return getString(R.string.error_load_area_list);
    }

    @Override
    protected List<Area> loadDataCore(Bundle bundle) throws Exception {
        List<Area> areaList = restService.getAreaListByCity(city);
        Collections.sort(areaList, initialComparator);
        return areaList;
    }

    @Override
    public void onAreaClick(String area) {
        final Intent intent = new Intent();
        intent.putExtra(AreaListActivity.INTENT_AREA, area);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
