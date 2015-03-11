package com.aumum.app.mobile.ui.saving;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.SavingCommentStore;
import com.aumum.app.mobile.core.dao.SavingStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Saving;
import com.aumum.app.mobile.core.model.SavingComment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.AddSavingCommentEvent;
import com.aumum.app.mobile.events.AddSavingCommentFinishedEvent;
import com.aumum.app.mobile.events.ReplySavingCommentEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.saving.SavingCommentCard;
import com.aumum.app.mobile.ui.saving.SavingCommentsAdapter;
import com.aumum.app.mobile.ui.saving.SavingDetailsActivity;
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

/**
 * Created by Administrator on 12/03/2015.
 */
public class SavingCommentsFragment extends ItemListFragment<SavingComment> {

    @Inject Bus bus;
    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject SavingStore savingStore;
    @Inject SavingCommentStore savingCommentStore;

    private ViewGroup mainView;

    private String savingId;
    private User currentUser;
    private Saving saving;

    private SafeAsyncTask<Boolean> task;

    public void setSaving(Saving saving) {
        this.saving = saving;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        savingId = intent.getStringExtra(SavingDetailsActivity.INTENT_SAVING_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saving_comments, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
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
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected ArrayAdapter<SavingComment> createAdapter(List<SavingComment> items) {
        return new SavingCommentsAdapter(getActivity(), items);
    }

    @Override
    protected List<SavingComment> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        saving = savingStore.getSavingByIdFromServer(savingId);
        if (saving.getDeletedAt() != null) {
            throw new Exception(getString(R.string.error_saving_comment_was_deleted));
        }
        List<SavingComment> result = savingCommentStore.getSavingComments(saving.getComments());
        for (SavingComment comment : result) {
            comment.setUser(userStore.getUserById(comment.getUserId()));
        }
        return result;
    }

    @Subscribe
    public void onAddSavingCommentEvent(final AddSavingCommentEvent event) {
        if (task != null) {
            return;
        }

        // update UI first
        SavingComment comment = new SavingComment(savingId,
                event.getRepliedId(), event.getContent(), currentUser.getObjectId());
        comment.setUser(currentUser);
        getData().add(0, comment);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                SavingComment comment = getData().get(0);
                final SavingComment newComment = new SavingComment(
                        comment.getParentId(),
                        comment.getRepliedId(),
                        comment.getContent(),
                        comment.getUserId());
                SavingComment response = restService.newSavingComment(newComment);
                restService.addSavingComment(savingId, response.getObjectId());
                saving.addComment(response.getObjectId());
                savingStore.save(saving);
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
                refresh(null);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
                refresh(null);
                bus.post(new AddSavingCommentFinishedEvent(event.getContent()));
            }
        };
        task.execute();
    }

    private void showActionDialog(View view) {
        final SavingCommentCard card = (SavingCommentCard) view.getTag();
        final SavingComment comment = card.getComment();
        final boolean isOwner = saving.isOwner(currentUser.getObjectId()) ||
                comment.isOwner(currentUser.getObjectId());
        List<String> options = new ArrayList<String>();
        options.add(getString(R.string.label_reply));
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
                                reply(comment);
                                break;
                            case 1:
                                if (isOwner) {
                                    deleteComment(card);
                                } else {
                                    reportComment(comment);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void reply(SavingComment comment) {
        bus.post(new ReplySavingCommentEvent(comment));
    }

    private void deleteComment(final SavingCommentCard card) {
        if (task != null) {
            return;
        }
        final SavingComment comment = card.getComment();
        card.onActionStart();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deleteSavingComment(comment.getObjectId(), comment.getParentId());
                saving.removeComment(comment.getObjectId());
                savingStore.save(saving);
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
                onCommentDeletedSuccess(comment.getObjectId());
            }

            @Override
            protected void onFinally() throws RuntimeException {
                card.onActionFinish();
                task = null;
            }
        };
        task.execute();
    }

    private void onCommentDeletedSuccess(String commentId) {
        List<SavingComment> commentList = getData();
        for (Iterator<SavingComment> it = commentList.iterator(); it.hasNext();) {
            SavingComment comment = it.next();
            if (comment.getObjectId().equals(commentId)) {
                it.remove();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getListAdapter().notifyDataSetChanged();
                    }
                });
                Toaster.showShort(getActivity(), R.string.info_comment_deleted);
                return;
            }
        }
    }

    private void reportComment(SavingComment comment) {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_SAVING_COMMENT);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, comment.getObjectId());
        startActivity(intent);
    }
}
