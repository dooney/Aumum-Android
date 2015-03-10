package com.aumum.app.mobile.ui.circle;

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
import com.aumum.app.mobile.events.NewAskingUnreadEvent;
import com.aumum.app.mobile.events.NewMomentUnreadEvent;
import com.aumum.app.mobile.events.ResetCircleUnreadEvent;
import com.aumum.app.mobile.ui.asking.AskingActivity;
import com.aumum.app.mobile.ui.moment.MomentsActivity;
import com.aumum.app.mobile.ui.special.SpecialActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Administrator on 2/03/2015.
 */
public class CircleFragment extends Fragment {

    @Inject Bus bus;

    private boolean resetUnread;

    private View askingLayout;
    private ImageView askingUnreadImage;
    private View momentsLayout;
    private ImageView momentUnreadImage;
    private View specialLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_circle, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        askingLayout = view.findViewById(R.id.layout_asking);
        askingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAskingActivity();
            }
        });
        askingUnreadImage = (ImageView) view.findViewById(R.id.image_asking_unread);
        momentsLayout = view.findViewById(R.id.layout_moments);
        momentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMomentsActivity();
            }
        });
        momentUnreadImage = (ImageView) view.findViewById(R.id.image_moment_unread);
        specialLayout = view.findViewById(R.id.layout_special);
        specialLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpecialActivity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        if (resetUnread) {
            bus.post(new ResetCircleUnreadEvent());
        }
        resetUnread = false;
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.GET_ASKING_LIST_REQ_CODE) {
            askingUnreadImage.setVisibility(View.INVISIBLE);
            resetUnread = true;
        } else if (requestCode == Constants.RequestCode.GET_MOMENT_LIST_REQ_CODE) {
            momentUnreadImage.setVisibility(View.INVISIBLE);
            resetUnread = true;
        }
    }

    private void startAskingActivity() {
        final Intent intent = new Intent(getActivity(), AskingActivity.class);
        startActivityForResult(intent, Constants.RequestCode.GET_ASKING_LIST_REQ_CODE);
    }

    private void startMomentsActivity() {
        final Intent intent = new Intent(getActivity(), MomentsActivity.class);
        startActivityForResult(intent, Constants.RequestCode.GET_MOMENT_LIST_REQ_CODE);
    }

    private void startSpecialActivity() {
        final Intent intent = new Intent(getActivity(), SpecialActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void onNewAskingUnreadEvent(NewAskingUnreadEvent event) {
        askingUnreadImage.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onNewMomentUnreadEvent(NewMomentUnreadEvent event) {
        momentUnreadImage.setVisibility(View.VISIBLE);
    }
}
