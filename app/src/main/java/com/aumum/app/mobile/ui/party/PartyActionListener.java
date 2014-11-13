package com.aumum.app.mobile.ui.party;

import android.app.Activity;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.vm.MessageVM;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.helper.MessageBuilder;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 21/10/2014.
 */
public class PartyActionListener {
    private Activity activity;
    private Party party;

    private SafeAsyncTask<Boolean> task;

    @Inject RestService service;
    @Inject
    MessageDeliveryService messageDeliveryService;

    protected OnActionListener onActionListener;

    protected OnProgressListener onProgressListener;

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public static interface OnActionListener {
        public void onPartyDeletedSuccess(String partyId);
        public void onPartySharedSuccess();
    }

    public static interface OnProgressListener {
        public void onPartyActionStart();
        public void onPartyActionFinish();
    }

    public PartyActionListener(Activity activity, Party party) {
        this.activity = activity;
        this.party = party;
        Injector.inject(this);
    }

    protected void deleteParty() {
        if (onProgressListener != null) {
            onProgressListener.onPartyActionStart();
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                service.deleteParty(party.getObjectId());
                service.removeUserPartyPost(party.getUserId(), party.getObjectId());
                for(String userId: party.getMembers()) {
                    service.removeUserParty(userId, party.getObjectId());

                    MessageVM message = MessageBuilder.buildPartyMessage(MessageVM.Type.PARTY_DELETE,
                            party.getUser(), userId, null, party);
                    messageDeliveryService.send(message);
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                    Toaster.showShort(activity, R.string.error_delete_party);
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (onActionListener != null) {
                    onActionListener.onPartyDeletedSuccess(party.getObjectId());
                    Toaster.showShort(activity, R.string.info_party_deleted);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (onProgressListener != null) {
                    onProgressListener.onPartyActionFinish();
                }
                task = null;
            }
        };
        task.execute();
    }

    protected void shareParty() {

    }
}
