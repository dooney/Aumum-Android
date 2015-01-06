package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Party;
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
public class PartyFavoriteListener implements FavoriteTextView.OnFavoriteListener {

    private SafeAsyncTask<Boolean> task;

    private Party party;

    @Inject RestService restService;
    @Inject PartyStore partyStore;
    @Inject UserStore userStore;

    public PartyFavoriteListener(Party party) {
        this.party = party;
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
                restService.removePartyFavorite(party.getObjectId(),
                        currentUser.getObjectId());
                party.removeFavorite(currentUser.getObjectId());
                partyStore.save(party);
                currentUser.removePartyFavorite(party.getObjectId());
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
                restService.addPartyFavorite(party.getObjectId(),
                        currentUser.getObjectId());
                party.addFavorite(currentUser.getObjectId());
                partyStore.save(party);
                currentUser.addPartyFavorite(party.getObjectId());
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
