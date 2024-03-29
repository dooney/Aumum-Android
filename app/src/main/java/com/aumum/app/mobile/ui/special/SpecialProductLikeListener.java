package com.aumum.app.mobile.ui.special;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.SpecialProduct;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.LikeTextView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 11/03/2015.
 */
public class SpecialProductLikeListener
        implements LikeTextView.OnLikeListener {

    @Inject RestService restService;
    @Inject ApiKeyProvider apiKeyProvider;

    private SpecialProduct specialProduct;
    private SafeAsyncTask<Boolean> task;

    public SpecialProductLikeListener(SpecialProduct specialProduct) {
        this.specialProduct = specialProduct;
        Injector.inject(this);
    }

    @Override
    public void onUnLike(LikeTextView view) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String currentUserId = apiKeyProvider.getAuthUserId();
                restService.removeSpecialProductLike(specialProduct.getObjectId(),
                        specialProduct.getSpecialId(), currentUserId);
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
    public void onLike(final LikeTextView view) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                String currentUserId = apiKeyProvider.getAuthUserId();
                restService.addSpecialProductLike(specialProduct.getObjectId(),
                        specialProduct.getSpecialId(), currentUserId);
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
