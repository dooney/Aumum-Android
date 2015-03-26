package com.aumum.app.mobile.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Game;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.browser.BrowserActivity;
import com.aumum.app.mobile.utils.UMengUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GameListFragment extends ItemListFragment<Game> {

    @Inject RestService restService;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Game game = getData().get(i);
                startGameActivity(game);
                updateClickCount(game.getSeq());
            }
        });
    }

    @Override
    protected ArrayAdapter<Game> createAdapter(List<Game> items) {
        return new GamesAdapter(getActivity(), items);
    }

    @Override
    protected List<Game> loadDataCore(Bundle bundle) throws Exception {
        return restService.getGameList();
    }

    private void updateClickCount(int id) {
        String eventId = "game_seq_" + id;
        UMengUtils.onEvent(getActivity(), eventId);
    }

    private void startGameActivity(Game game) {
        final Intent intent = new Intent(getActivity(), BrowserActivity.class);
        intent.putExtra(BrowserActivity.INTENT_URL, game.getUri());
        intent.putExtra(BrowserActivity.INTENT_FULLSCREEN, true);
        startActivity(intent);
    }
}
