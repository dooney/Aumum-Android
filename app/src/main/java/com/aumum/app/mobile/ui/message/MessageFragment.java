package com.aumum.app.mobile.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.events.ResetMessageUnreadEvent;
import com.aumum.app.mobile.ui.contact.ContactRequestsActivity;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by Administrator on 14/05/2015.
 */
public class MessageFragment extends Fragment {

    @Inject MessageStore messageStore;
    @Inject Bus bus;

    private ImageView contactRequestsUnreadImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View contactRequestsLayout = view.findViewById(R.id.layout_contact_requests);
        contactRequestsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(
                        getActivity(), ContactRequestsActivity.class);
                startActivity(intent);
            }
        });
        contactRequestsUnreadImage = (ImageView) view.findViewById(
                R.id.image_contact_requests_unread);
        updateUnread();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        updateUnread();
    }

    @Override
    public void onPause() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateUnread();
            bus.post(new ResetMessageUnreadEvent());
        }
    }

    private void updateUnread() {
        if (contactRequestsUnreadImage != null) {
            if (messageStore.hasContactRequestsUnread()) {
                contactRequestsUnreadImage.setVisibility(View.VISIBLE);
            } else {
                contactRequestsUnreadImage.setVisibility(View.GONE);
            }
        }
    }
}
