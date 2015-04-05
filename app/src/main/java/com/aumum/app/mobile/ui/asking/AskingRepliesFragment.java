package com.aumum.app.mobile.ui.asking;

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
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.service.ShareService;
import com.aumum.app.mobile.events.AddAskingReplyEvent;
import com.aumum.app.mobile.events.ReplyAskingReplyEvent;
import com.aumum.app.mobile.ui.base.RefreshItemListFragment;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.view.ListViewDialog;
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
    @Inject AskingReplyStore askingReplyStore;
    @Inject UserStore userStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject Bus bus;
    @Inject ChatService chatService;
    private ShareService shareService;

    private Asking asking;
    private User currentUser;
    private AskingReply replied;
    private List<AskingReply> dataSet;

    private ViewGroup mainView;

    private SafeAsyncTask<Boolean> task;

    public void setAsking(Asking asking) {
        this.asking = asking;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        dataSet = new ArrayList<AskingReply>();
        shareService = new ShareService(getActivity());
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
                showActionDialog(view);
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
        return new AskingRepliesAdapter(getActivity(), items, asking);
    }

    @Override
    protected List<AskingReply> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        return super.loadDataCore(bundle);
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected void getUpwardsList() {
        List<AskingReply> askingReplies = askingReplyStore.getUpwardsList(asking.getReplies());
        dataSet.addAll(askingReplies);
    }

    @Override
    protected void getBackwardsList() {
        if (dataSet.size() > 0) {
            AskingReply last = dataSet.get(dataSet.size() - 1);
            List<AskingReply> askingReplies = askingReplyStore.getBackwardsList(asking.getReplies(),
                    last.getCreatedAt());
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
        int totalCount = dataSet.size();
        if (totalCount < AskingReplyStore.LIMIT_PER_LOAD) {
            setMore(false);
        }
        if (totalCount > 0) {
            for (AskingReply askingReply : dataSet) {
                if (askingReply.getUser() == null) {
                    askingReply.setUser(userStore.getUserById(askingReply.getUserId()));
                }
            }
        }
        return dataSet;
    }

    @Subscribe
    public void onAddAskingReplyEvent(AddAskingReplyEvent event) {
        if (task != null) {
            return;
        }

        // update UI first
        String repliedId = null;
        String content = event.getReply();
        if (replied != null) {
            repliedId = replied.getObjectId();
            if (!replied.getIsAnonymous()) {
                content = getString(R.string.hint_reply_asking_reply,
                        replied.getUser().getScreenName(), content);
            }
        }
        final AskingReply askingReply = new AskingReply(currentUser.getObjectId(),
                content, repliedId, asking.getIsAnonymous());
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
                        reply.getContent(), reply.getRepliedId(), reply.getIsAnonymous());
                AskingReply response = restService.newAskingReply(newReply);
                restService.addAskingReplies(asking.getObjectId(), response.getObjectId());
                asking.addReply(response.getObjectId());
                askingStore.save(asking);
                if (!asking.isOwner(currentUser.getObjectId())) {
                    sendAskingReplyMessage(asking, newReply);
                    updateCredit(currentUser, CreditRule.ADD_ASKING_REPLY);
                }
                if (replied != null &&
                    !asking.isOwner(replied.getUserId()) &&
                    !replied.isOwner(currentUser.getObjectId())) {
                    sendAskingRepliedMessage(asking, replied, newReply);
                    updateCredit(currentUser, CreditRule.ADD_ASKING_REPLY);
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(getActivity(), cause.getMessage());
                    }
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                dataSet.clear();
                refresh(null);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
                replied = null;
            }
        };
        task.execute();
    }

    private void sendAskingReplyMessage(Asking asking, AskingReply askingReply) throws Exception {
        String title = getString(R.string.label_reply_asking_message,
                currentUser.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.ASKING_REPLY,
                title, askingReply.getContent(), asking.getObjectId());
        chatService.sendCmdMessage(asking.getUser().getChatId(), cmdMessage, false, null);
    }

    private void sendAskingRepliedMessage(Asking asking,
                                          AskingReply replied,
                                          AskingReply askingReply) throws Exception {
        String title = getString(R.string.label_replied_asking_message,
                currentUser.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.ASKING_REPLIED,
                title, askingReply.getContent(), asking.getObjectId());
        chatService.sendCmdMessage(replied.getUser().getChatId(), cmdMessage, false, null);
    }

    private void reportAskingReply(AskingReply askingReply) {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_ASKING_REPLY);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, askingReply.getObjectId());
        startActivity(intent);
    }

    private void showActionDialog(View view) {
        final AskingReplyCard card = (AskingReplyCard) view.getTag();
        final AskingReply askingReply = card.getAskingReply();
        final boolean isOwner = asking.isOwner(currentUser.getObjectId()) ||
                askingReply.isOwner(currentUser.getObjectId());
        List<String> options = new ArrayList<String>();
        options.add(getString(R.string.label_reply));
        options.add(getString(R.string.label_share));
        if (isOwner) {
            options.add(getString(R.string.label_delete));
        } else {
            options.add(getString(R.string.label_report));
        }
        new ListViewDialog(getActivity(), null, options,
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 0:
                        reply(askingReply);
                        break;
                    case 1:
                        showShare(askingReply);
                        break;
                    case 2:
                        if (isOwner) {
                            deleteAskingReply(card);
                        } else {
                            reportAskingReply(askingReply);
                        }
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void reply(AskingReply askingReply) {
        replied = askingReply;
        String screenName = replied.getIsAnonymous() ?
                getString(R.string.label_anonymous) :
                askingReply.getUser().getScreenName();
        String replyHint = getString(R.string.hint_reply_asking_reply,
                screenName, askingReply.getContent());
        bus.post(new ReplyAskingReplyEvent(replyHint));
    }

    private void showShare(AskingReply askingReply) {
        shareService.show(askingReply.getContent(), null, null);
    }

    private void deleteAskingReply(final AskingReplyCard card) {
        if (task != null) {
            return;
        }
        final AskingReply askingReply = card.getAskingReply();
        card.onActionStart();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteAskingReply(askingReply.getObjectId(), asking.getObjectId());
                updateCredit(currentUser, CreditRule.DELETE_ASKING_REPLY);
                asking.removeReply(askingReply.getObjectId());
                askingStore.save(asking);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(getActivity(), cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) throws Exception {
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

    private void onAskingReplyDeletedSuccess(String askingReplyId) throws Exception {
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
    }

    private void updateCredit(User currentUser, int seq) throws Exception {
        final CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            final int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
            userStore.save(currentUser);
            if (credit > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.showShort(getActivity(), getString(R.string.info_got_credit,
                                creditRule.getDescription(), credit));
                    }
                });
            }
        }
    }
}
