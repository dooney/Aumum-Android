package com.aumum.app.mobile.ui.asking;

import android.app.Activity;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.AskingGroup;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 2/04/2015.
 */
public class AskingGroupJoinListener {

    @Inject RestService restService;
    @Inject UserStore userStore;

    public interface OnActionListener {
        public void onStart();
        public void onException(Exception e);
        public void onSuccess();
    }

    public AskingGroupJoinListener() {
        Injector.inject(this);
    }

    public void onJoin(final Activity activity,
                       final AskingGroup askingGroup,
                       final OnActionListener listener) {
        if (listener != null) {
            listener.onStart();
        }
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                restService.addUserAskingGroup(currentUser.getObjectId(), askingGroup.getObjectId());
                currentUser.addAskingGroup(askingGroup.getObjectId());
                userStore.save(currentUser);
                return true;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(activity, cause.getMessage());
                    }
                }
                if (listener != null) {
                    listener.onException(e);
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        }.execute();
    }
}
