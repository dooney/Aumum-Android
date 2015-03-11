package com.aumum.app.mobile.ui.special;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.FavoriteTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 11/03/2015.
 */
public class SpecialProductFavoriteListener
        implements FavoriteTextView.OnFavoriteListener {

    @Inject RestService restService;
    @Inject UserStore userStore;

    private SpecialProduct specialProduct;
    private SafeAsyncTask<Boolean> task;

    public SpecialProductFavoriteListener(SpecialProduct specialProduct) {
        this.specialProduct = specialProduct;
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
                restService.removeSpecialProductFavorite(specialProduct.getObjectId(),
                        currentUser.getObjectId());
                currentUser.removeSpecialFavorite(specialProduct.getObjectId());
                userStore.save(currentUser);
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
                restService.addSpecialProductFavorite(specialProduct.getObjectId(),
                        currentUser.getObjectId());
                currentUser.addSpecialFavorite(specialProduct.getObjectId());
                userStore.save(currentUser);
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
