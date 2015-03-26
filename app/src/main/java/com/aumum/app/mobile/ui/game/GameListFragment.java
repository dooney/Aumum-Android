package com.aumum.app.mobile.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.model.Game;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.RefreshGameEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.browser.BrowserActivity;
import com.aumum.app.mobile.utils.UMengUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 26/03/2015.
 */
public class GameListFragment extends ItemListFragment<Game>
        implements GameClickListener {

    @Inject Bus bus;
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
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    protected ArrayAdapter<Game> createAdapter(List<Game> items) {
        return new GamesAdapter(getActivity(), items, this);
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
        intent.putExtra(BrowserActivity.INTENT_LANDSCAPE, game.isLandscape());
        startActivity(intent);
    }

    @Subscribe
    public void onRefreshGameEvent(RefreshGameEvent event) {
        getMainView().setVisibility(View.GONE);
        reload();
    }

    @Override
    public void onClick(Game game) {
        startGameActivity(game);
        updateClickCount(game.getSeq());
    }
}
