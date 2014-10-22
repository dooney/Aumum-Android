package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.service.MessageListener;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.MessageEvent;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 21/10/2014.
 */
public class PartyActionListener {
    private Party party;

    private SafeAsyncTask<Boolean> task;

    @Inject RestService service;
    @Inject MessageListener messageListener;

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

    public PartyActionListener(Party party) {
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

                    messageListener.onMessageEvent(new MessageEvent(
                            Message.DELETE_PARTY, userId, party.getUserId()));
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
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (onActionListener != null) {
                    onActionListener.onPartyDeletedSuccess(party.getObjectId());
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
