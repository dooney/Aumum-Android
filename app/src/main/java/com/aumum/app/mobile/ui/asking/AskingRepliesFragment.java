package com.aumum.app.mobile.ui.asking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.AskingReplyStore;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.AddAskingReplyEvent;
import com.aumum.app.mobile.events.AddAskingReplyFinishedEvent;
import com.aumum.app.mobile.events.ReplyAskingReplyEvent;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

public class AskingRepliesFragment extends RefreshItemListFragment<AskingReply> {

    @Inject RestService restService;
    @Inject AskingStore askingStore;
    @Inject AskingReplyStore dataStore;
    @Inject UserStore userStore;
    @Inject MessageDeliveryService messageDeliveryService;
    @Inject Bus bus;

    private String askingId;
    private Asking asking;
    private User currentUser;
    private AskingReply replied;
    private List<AskingReply> dataSet;

    private ViewGroup mainView;

    private SafeAsyncTask<Boolean> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        askingId = intent.getStringExtra(AskingDetailsActivity.INTENT_ASKING_ID);

        dataSet = new ArrayList<AskingReply>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asking_replies, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = (ViewGroup) view.findViewById(R.id.main_view);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AskingReply askingReply = getData().get(position);
                if (asking.isOwner(currentUser.getObjectId()) ||
                        askingReply.isOwner(currentUser.getObjectId())) {
                    showActionDialog(view);
                } else {
                    reply(askingReply);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected ArrayAdapter<AskingReply> createAdapter(List<AskingReply> items) {
        return new AskingRepliesAdapter(getActivity(), items);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_asking_reply_list;
    }

    @Override
    protected List<AskingReply> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        asking = askingStore.getAskingByIdFromServer(askingId);
        return super.loadDataCore(bundle);
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected void getUpwardsList() {
        List<AskingReply> askingReplies = dataStore.getUpwardsList(asking.getReplies());
        dataSet.addAll(askingReplies);
    }

    @Override
    protected void getBackwardsList() {
        if (dataSet.size() > 0) {
            AskingReply last = dataSet.get(dataSet.size() - 1);
            List<AskingReply> askingReplies = dataStore.getBackwardsList(asking.getReplies(), last.getCreatedAt());
            dataSet.addAll(askingReplies);
            if (askingReplies.size() > 0) {
                setMore(true);
            } else {
                setMore(false);
            }
        }
    }

    @Override
    protected List<AskingReply> buildCards() throws Exception {
        List<AskingReply> cards = new ArrayList<AskingReply>();
        if (dataSet.size() > 0) {
            for (AskingReply askingReply : dataSet) {
                if (askingReply.getUser() == null) {
                    askingReply.setUser(userStore.getUserById(askingReply.getUserId()));
                }
                cards.add(askingReply);
            }
        }
        return cards;
    }

    @Subscribe
    public void onAddAskingReplyEvent(AddAskingReplyEvent event) {
        if (task != null) {
            return;
        }

        // update UI first
        final String repliedId = (replied != null) ? replied.getObjectId() : null;
        final AskingReply askingReply = new AskingReply(currentUser.getObjectId(),
                event.getReply(), repliedId);
        askingReply.setUser(currentUser);
        getData().add(0, askingReply);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                AskingReply reply = getData().get(0);
                final AskingReply newReply = new AskingReply(reply.getUserId(),
                        reply.getContent(), repliedId);

                // asking reply
                AskingReply response = restService.newAskingReply(newReply);
                restService.addAskingReplies(askingId, response.getObjectId());
                reply.setObjectId(response.getObjectId());
                reply.setCreatedAt(response.getCreatedAt());

                Message message = new Message(Message.Type.ASKING_REPLY_NEW,
                        currentUser.getObjectId(), asking.getUserId(), reply.getContent(), askingId);
                messageDeliveryService.send(message);
                if (replied != null) {
                    Message repliedMessage = new Message(Message.Type.ASKING_REPLY_REPLY,
                            currentUser.getObjectId(), replied.getUserId(), askingReply.getContent(), askingId);
                    messageDeliveryService.send(repliedMessage);
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
            protected void onFinally() throws RuntimeException {
                task = null;
                replied = null;
                getListAdapter().notifyDataSetChanged();
                show();
                bus.post(new AddAskingReplyFinishedEvent());
            }
        };
        task.execute();
    }

    private void showActionDialog(View view) {
        final AskingReplyCard card = (AskingReplyCard) view.getTag();
        final String options[] = getResources().getStringArray(R.array.label_asking_reply_actions);
        DialogUtils.showDialog(getActivity(), options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        reply(card.getAskingReply());
                        break;
                    case 1:
                        break;
                    case 2:
                        deleteAskingReply(card);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void reply(AskingReply askingReply) {
        replied = askingReply;
        String replyHint = getString(R.string.hint_reply_asking_reply,
                askingReply.getUser().getScreenName(), askingReply.getContent());
        bus.post(new ReplyAskingReplyEvent(replyHint));
    }

    private void deleteAskingReply(final AskingReplyCard card) {
        final AskingReply askingReply = card.getAskingReply();

        card.onActionStart();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteAskingReply(askingReply.getObjectId());
                restService.removeAskingReplies(askingId, askingReply.getObjectId());
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
                Toaster.showShort(getActivity(), R.string.error_delete_asking_reply);
            }

            @Override
            public void onSuccess(final Boolean success) {
                onAskingReplyDeletedSuccess(askingReply.getObjectId());
            }

            @Override
            protected void onFinally() throws RuntimeException {
                card.onActionFinish();
                task = null;
            }
        };
        task.execute();
    }

    private void onAskingReplyDeletedSuccess(String askingReplyId) {
        try {
            List<AskingReply> askingReplies = getData();
            for (Iterator<AskingReply> it = askingReplies.iterator(); it.hasNext();) {
                AskingReply askingReply = it.next();
                if (askingReply.getObjectId().equals(askingReplyId)) {
                    it.remove();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getListAdapter().notifyDataSetChanged();
                        }
                    });
                    Toaster.showShort(getActivity(), R.string.info_asking_reply_deleted);
                    return;
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
        Toaster.showShort(getActivity(), R.string.error_delete_asking_reply);
    }
}
