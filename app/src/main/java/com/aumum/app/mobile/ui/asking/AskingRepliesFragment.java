package com.aumum.app.mobile.ui.asking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.AskingReplyStore;
import com.aumum.app.mobile.core.dao.AskingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.AskingReply;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.AddAskingReplyEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

public class AskingRepliesFragment extends ItemListFragment<AskingReply> {

    @Inject RestService service;
    @Inject AskingStore askingStore;
    @Inject AskingReplyStore askingReplyStore;
    @Inject UserStore userStore;
    @Inject Bus bus;

    private String askingId;
    private User currentUser;

    private ViewGroup mainView;

    private SafeAsyncTask<Boolean> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        askingId = intent.getStringExtra(AskingDetailsActivity.INTENT_ASKING_ID);
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
        Asking asking = askingStore.getById(askingId);
        List<AskingReply> result = askingReplyStore.getAskingReplies(asking.getReplies());
        for (AskingReply askingReply: result) {
            askingReply.setUser(userStore.getUserById(askingReply.getUserId()));
        }
        return result;
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Subscribe
    public void onAddAskingReplyEvent(AddAskingReplyEvent event) {
        if (task != null) {
            return;
        }

        // update UI first
        AskingReply askingReply = new AskingReply(currentUser.getObjectId(), event.getReply());
        askingReply.setUser(currentUser);
        getData().add(0, askingReply);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                AskingReply reply = getData().get(0);
                final AskingReply newReply = new AskingReply(reply.getUserId(), reply.getContent());

                // asking reply
                AskingReply response = service.newAskingReply(newReply);
                service.addAskingReplies(askingId, response.getObjectId());
                reply.setObjectId(response.getObjectId());
                reply.setCreatedAt(response.getCreatedAt());

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
                getListAdapter().notifyDataSetChanged();
                show();
            }
        };
        task.execute();
    }
}
