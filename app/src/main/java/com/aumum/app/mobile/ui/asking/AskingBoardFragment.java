package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.AskingBoard;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemListFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 2/04/2015.
 */
public class AskingBoardFragment extends ItemListFragment<AskingBoard> {

    @Inject RestService restService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking_board, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AskingBoard askingBoard = getData().get(i);
                startAskingGroupListActivity(askingBoard);
            }
        });
    }

    @Override
    protected ArrayAdapter<AskingBoard> createAdapter(List<AskingBoard> items) {
        return new AskingBoardAdapter(getActivity(), items);
    }

    @Override
    protected List<AskingBoard> loadDataCore(Bundle bundle) throws Exception {
        return restService.getAskingBoardList();
    }

    private void startAskingGroupListActivity(AskingBoard askingBoard) {
        final Intent intent = new Intent(getActivity(), AskingGroupListActivity.class);
        intent.putExtra(AskingGroupListActivity.INTENT_TITLE, askingBoard.getScreenName());
        intent.putExtra(AskingGroupListActivity.INTENT_BOARD_ID, askingBoard.getObjectId());
        startActivity(intent);
    }
}
