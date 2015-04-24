package com.aumum.app.mobile.ui.chat;

import android.app.Activity;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.LogoutService;
import com.aumum.app.mobile.events.LogoutEvent;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by Administrator on 3/01/2015.
 */
public class ChatConnectionListener implements EMConnectionListener {

    @Inject LogoutService logoutService;
    @Inject Bus bus;

    private Activity activity;
    private ConfirmDialog connectionConflictDialog;

    public ChatConnectionListener(Activity activity) {
        this.activity = activity;
        Injector.inject(this);
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected(final int error) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (error) {
                    case EMError.CONNECTION_CONFLICT:
                        showConnectionConflictDialog();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showConnectionConflictDialog() {
        if (connectionConflictDialog == null) {
            connectionConflictDialog = new ConfirmDialog(activity,
                    R.layout.dialog_connection_conflict,
                    new ConfirmDialog.OnConfirmListener() {
                        @Override
                        public void call(Object value) throws Exception {
                            logoutService.logout();
                        }

                        @Override
                        public void onException(String errorMessage) {

                        }

                        @Override
                        public void onSuccess(Object value) {
                            bus.post(new LogoutEvent());
                        }
                    });
        }
        connectionConflictDialog.show();
    }
}
