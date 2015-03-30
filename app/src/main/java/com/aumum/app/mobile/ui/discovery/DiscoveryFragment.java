package com.aumum.app.mobile.ui.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.events.NewMomentUnreadEvent;
import com.aumum.app.mobile.events.NewPartyUnreadEvent;
import com.aumum.app.mobile.events.ResetDiscoveryUnreadEvent;
import com.aumum.app.mobile.ui.feed.article.ArticleListActivity;
import com.aumum.app.mobile.ui.feed.channel.ChannelListActivity;
import com.aumum.app.mobile.ui.game.GameListActivity;
import com.aumum.app.mobile.ui.moment.MomentListActivity;
import com.aumum.app.mobile.ui.party.PartyActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 16/03/2015.
 */
public class DiscoveryFragment extends Fragment {

    @Inject Bus bus;

    private boolean unreadParty;
    private boolean unreadMoment;

    @InjectView(R.id.layout_weibo)protected View weiboLayout;
    @InjectView(R.id.layout_wechat)protected View wechatLayout;
    @InjectView(R.id.layout_game)protected View gameLayout;
    @InjectView(R.id.layout_party)protected View partyLayout;
    @InjectView(R.id.image_party_unread)protected ImageView partyUnreadImage;
    @InjectView(R.id.layout_moment)protected View momentLayout;
    @InjectView(R.id.image_moment_unread)protected ImageView momentUnreadImage;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ButterKnife.inject(this, getView());
        weiboLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChannelListActivity();
            }
        });
        wechatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startArticleListActivity();
            }
        });
        gameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGameListActivity();
            }
        });
        partyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPartyActivity();
            }
        });
        momentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMomentListActivity();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.GET_PARTY_REQ_CODE) {
            partyUnreadImage.setVisibility(View.GONE);
            unreadParty = false;
        } else if (requestCode == Constants.RequestCode.GET_MOMENT_REQ_CODE) {
            momentUnreadImage.setVisibility(View.GONE);
            unreadMoment = false;
        }
        if (!unreadParty && !unreadMoment) {
            bus.post(new ResetDiscoveryUnreadEvent());
        }
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

    private void startChannelListActivity() {
        final Intent intent = new Intent(getActivity(), ChannelListActivity.class);
        startActivity(intent);
    }

    private void startArticleListActivity() {
        final Intent intent = new Intent(getActivity(), ArticleListActivity.class);
        startActivity(intent);
    }

    private void startGameListActivity() {
        final Intent intent = new Intent(getActivity(), GameListActivity.class);
        startActivity(intent);
    }

    private void startPartyActivity() {
        final Intent intent = new Intent(getActivity(), PartyActivity.class);
        startActivityForResult(intent, Constants.RequestCode.GET_PARTY_REQ_CODE);
    }

    private void startMomentListActivity() {
        final Intent intent = new Intent(getActivity(), MomentListActivity.class);
        startActivityForResult(intent, Constants.RequestCode.GET_MOMENT_REQ_CODE);
    }

    @Subscribe
    public void onNewPartyUnreadEvent(NewPartyUnreadEvent event) {
        partyUnreadImage.setVisibility(View.VISIBLE);
        unreadParty = true;
    }

    @Subscribe
    public void onNewMomentUnreadEvent(NewMomentUnreadEvent event) {
        momentUnreadImage.setVisibility(View.VISIBLE);
        unreadMoment = true;
    }
}
