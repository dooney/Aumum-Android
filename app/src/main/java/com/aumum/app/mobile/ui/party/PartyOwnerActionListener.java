package com.aumum.app.mobile.ui.party;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.DeletePartyEvent;
import com.aumum.app.mobile.events.MessageEvent;
import com.aumum.app.mobile.ui.view.DropdownImageView;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 21/10/2014.
 */
public class PartyOwnerActionListener implements DropdownImageView.OnItemClickListener {
    private final String items[] = {"分享", "删除"};

    private Party party;

    private SafeAsyncTask<Boolean> task;

    @Inject RestService service;
    @Inject Bus bus;

    public PartyOwnerActionListener(Party party) {
        this.party = party;
        Injector.inject(this);
        bus.register(this);
    }

    @Override
    public void onItemClick(int item) {
        switch (item) {
            case 0:
                break;
            case 1:
                deleteParty();
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getItems() {
        return items;
    }

    private void deleteParty() {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                service.deleteParty(party.getObjectId());
                service.removeUserPartyPost(party.getUserId(), party.getObjectId());
                for(String userId: party.getMembers()) {
                    service.removeUserParty(userId, party.getObjectId());

                    bus.post(new MessageEvent(Message.DELETE_PARTY, userId, party.getUserId()));
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
                bus.post(new DeletePartyEvent(party.getObjectId()));
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }
}