package com.aumum.app.mobile.ui.asking;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 2/12/2014.
 */
public class AskingFavoriteListener implements FavoriteTextView.OnFavoriteListener {

    private SafeAsyncTask<Boolean> task;

    private Asking asking;

    @Inject RestService restService;
    @Inject AskingStore askingStore;
    @Inject UserStore userStore;

    public AskingFavoriteListener(Asking asking) {
        this.asking = asking;
        Injector.inject(this);
    }
    @Override
    public void onUnFavorite(FavoriteTextView view) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                restService.removeAskingFavorite(asking.getObjectId(),
                        currentUser.getObjectId());
                askingStore.removeFavorite(asking.getObjectId(),
                        currentUser.getObjectId());
                userStore.removeAskingFavorite(currentUser.getObjectId(),
                        asking.getObjectId());
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onFavorite(FavoriteTextView view) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User currentUser = userStore.getCurrentUser();
                restService.addAskingFavorite(asking.getObjectId(),
                        currentUser.getObjectId());
                askingStore.addFavorite(asking.getObjectId(),
                        currentUser.getObjectId());
                userStore.addAskingFavorite(currentUser.getObjectId(),
                        asking.getObjectId());
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }
}
