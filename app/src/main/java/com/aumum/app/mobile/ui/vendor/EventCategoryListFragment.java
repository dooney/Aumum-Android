package com.aumum.app.mobile.ui.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.EventCategory;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 21/03/2015.
 */
public class EventCategoryListFragment extends ItemListFragment<EventCategory> {

    @Inject RestService restService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_category_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventCategory eventCategory = getData().get(i);
                startEventListActivity(eventCategory);
            }
        });
    }

    @Override
    protected ArrayAdapter<EventCategory> createAdapter(List<EventCategory> items) {
        return new EventCategoriesAdapter(getActivity(), items);
    }

    @Override
    protected List<EventCategory> loadDataCore(Bundle bundle) throws Exception {
        return restService.getEventCategoryList();
    }

    private void startEventListActivity(EventCategory eventCategory) {
        final Intent intent = new Intent(getActivity(), EventListActivity.class);
        intent.putExtra(EventListActivity.INTENT_TITLE, eventCategory.getName());
        intent.putExtra(EventListActivity.INTENT_CATEGORY, eventCategory.getCategory());
        startActivity(intent);
    }
}