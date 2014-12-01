package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.aumum.app.mobile.events.AddAskingReplyFinishedEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

public class AskingRepliesFragment extends ItemListFragment<AskingReply> {

    @Inject RestService restService;
    @Inject AskingStore askingStore;
    @Inject AskingReplyStore askingReplyStore;
    @Inject UserStore userStore;
    @Inject Bus bus;

    private String askingId;
    private Asking asking;
    private User currentUser;

    private ViewGroup mainView;

    private SafeAsyncTask<Boolean> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        final Intent intent = getActivity().getIntent();
        askingId = intent.getStringExtra(AskingDetailsActivity.INTENT_ASKING_ID);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable() || asking == null) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog(asking.isOwner(currentUser.getObjectId()));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        asking = askingStore.getAskingByIdFromServer(askingId);
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
                AskingReply response = restService.newAskingReply(newReply);
                restService.addAskingReplies(askingId, response.getObjectId());
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
                bus.post(new AddAskingReplyFinishedEvent());
            }
        };
        task.execute();
    }

    private void showActionDialog(boolean isOwner) {
        List<String> options = new ArrayList<String>();
        options.add(getString(R.string.label_favorite));
        if (isOwner) {
            options.add(getString(R.string.label_delete));
        }
        DialogUtils.showDialog(getActivity(), options.toArray(new CharSequence[options.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                break;
                            case 1:
                                deleteAsking();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void deleteAsking() {
        showProgress();

        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteAsking(asking.getObjectId());
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Ln.e(e.getCause(), cause.getMessage());
                    }
                    Toaster.showShort(getActivity(), R.string.error_delete_asking);
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                Toaster.showShort(getActivity(), R.string.info_asking_deleted);

                final Intent intent = new Intent();
                intent.putExtra(AskingDetailsActivity.INTENT_ASKING_ID, askingId);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                task = null;
            }
        };
        task.execute();
    }
}
