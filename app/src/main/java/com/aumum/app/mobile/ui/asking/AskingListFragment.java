package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AskingListFragment extends RefreshItemListFragment<Asking> {

    @Inject AskingStore askingStore;
    @Inject UserStore userStore;

    private String groupId;
    private String title;
    private List<Asking> dataSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        groupId = intent.getStringExtra(AskingListActivity.INTENT_GROUP_ID);
        title = intent.getStringExtra(AskingListActivity.INTENT_TITLE);

        dataSet = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem plus = menu.add(Menu.NONE, 0, Menu.NONE, null);
        plus.setActionView(R.layout.menuitem_plus);
        plus.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View plusView = plus.getActionView();
        ImageView plusIcon = (ImageView) plusView.findViewById(R.id.b_plus);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    final Intent intent = new Intent(getActivity(), NewAskingActivity.class);
                    intent.putExtra(NewAskingActivity.INTENT_TITLE, title);
                    intent.putExtra(NewAskingActivity.INTENT_GROUP_ID, groupId);
                    startActivityForResult(intent, Constants.RequestCode.NEW_ASKING_REQ_CODE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        emptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.GET_ASKING_DETAILS_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String askingId = data.getStringExtra(AskingDetailsActivity.INTENT_ASKING_ID);
            if (askingId != null) {
                onAskingDeleted(askingId);
            }
        } else if (requestCode == Constants.RequestCode.NEW_ASKING_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            refresh(null);
        }
    }

    @Override
    protected ArrayAdapter<Asking> createAdapter(List<Asking> items) {
        return new AskingListAdapter(getActivity(), items);
    }

    @Override
    protected List<Asking> buildCards() throws Exception {
        int totalCount = dataSet.size();
        if (totalCount < AskingStore.LIMIT_PER_LOAD) {
            setMore(false);
        }
        if (totalCount > 0) {
            for (Asking asking : dataSet) {
                if (asking.getUser() == null) {
                    asking.setUser(userStore.getUserById(asking.getUserId()));
                }
            }
        }
        return dataSet;
    }

    @Override
    protected void getUpwardsList() throws Exception {
        String after = null;
        if (dataSet.size() > 0) {
            after = dataSet.get(0).getUpdatedAt();
        }
        List<Asking> askingList = askingStore.getUpwardsList(groupId, after);
        Collections.reverse(askingList);
        for(Asking asking: askingList) {
            for (Iterator<Asking> it = dataSet.iterator(); it.hasNext();) {
                Asking item = it.next();
                if (asking.getObjectId().equals(item.getObjectId())) {
                    it.remove();
                    break;
                }
            }
            dataSet.add(0, asking);
        }
    }

    @Override
    protected void getBackwardsList() throws Exception {
        if (dataSet.size() > 0) {
            Asking last = dataSet.get(dataSet.size() - 1);
            List<Asking> askingList = askingStore.getBackwardsList(groupId,
                    last.getUpdatedAt());
            dataSet.addAll(askingList);
            if (askingList.size() > 0) {
                setMore(true);
            } else {
                setMore(false);
            }
        }
    }

    private void onAskingDeleted(String askingId) {
        for (Iterator<Asking> it = dataSet.iterator(); it.hasNext();) {
            Asking asking = it.next();
            if (asking.getObjectId().equals(askingId)) {
                it.remove();
            }
        }
        for (Iterator<Asking> it = getData().iterator(); it.hasNext();) {
            Asking asking = it.next();
            if (asking.getObjectId().equals(askingId)) {
                it.remove();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getListAdapter().notifyDataSetChanged();
                    }
                });
                return;
            }
        }
    }

    @Override
    protected String getFragmentName() {
        return getClass().getSimpleName() + groupId;
    }
}
