package com.aumum.app.mobile.ui.moment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MomentCommentStore;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.MomentComment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.events.AddMomentCommentEvent;
import com.aumum.app.mobile.events.AddMomentCommentFinishedEvent;
import com.aumum.app.mobile.events.ReplyMomentCommentEvent;
import com.aumum.app.mobile.ui.base.ItemListFragment;
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
 * Created by Administrator on 3/03/2015.
 */
public class MomentCommentsFragment extends ItemListFragment<MomentComment> {

    @Inject Bus bus;
    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject MomentCommentStore momentCommentStore;

    private ViewGroup mainView;

    private String momentId;
    private User currentUser;
    private Moment moment;

    private SafeAsyncTask<Boolean> task;

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        momentId = intent.getStringExtra(MomentDetailsActivity.INTENT_MOMENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moment_comments, null);
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
    protected ArrayAdapter<MomentComment> createAdapter(List<MomentComment> items) {
        return new MomentCommentsAdapter(getActivity(), items);
    }

    @Override
    protected List<MomentComment> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        moment = momentStore.getMomentByIdFromServer(momentId);
        if (moment.getDeletedAt() != null) {
            throw new Exception(getString(R.string.error_party_was_deleted));
        }
        List<MomentComment> result = momentCommentStore.getMomentComments(moment.getComments());
        for (MomentComment comment : result) {
            comment.setUser(userStore.getUserById(comment.getUserId()));
        }
        return result;
    }

    @Subscribe
    public void onAddMomentCommentEvent(final AddMomentCommentEvent event) {
        if (task != null) {
            return;
        }

        // update UI first
        MomentComment comment = new MomentComment(momentId,
                event.getRepliedId(), event.getContent(), currentUser.getObjectId());
        comment.setUser(currentUser);
        getData().add(0, comment);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                MomentComment comment = getData().get(0);
                final MomentComment newComment = new MomentComment(
                        comment.getParentId(),
                        comment.getRepliedId(),
                        comment.getContent(),
                        comment.getUserId());
                MomentComment response = restService.newMomentComment(newComment);
                restService.addMomentComment(momentId, response.getObjectId());
                moment.addComment(response.getObjectId());
                momentStore.save(moment);
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
                bus.post(new AddMomentCommentFinishedEvent(event.getContent()));
            }
        };
        task.execute();
    }

    private void showActionDialog(View view) {
        final MomentCommentCard card = (MomentCommentCard) view.getTag();
        final MomentComment comment = card.getComment();
        final boolean isOwner = moment.isOwner(currentUser.getObjectId()) ||
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

    private void reply(MomentComment comment) {
        bus.post(new ReplyMomentCommentEvent(comment));
    }

    private void deleteComment(final MomentCommentCard card) {
        if (task != null) {
            return;
        }
        final MomentComment comment = card.getComment();
        card.onActionStart();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deletePartyComment(comment.getObjectId(), comment.getParentId());
                moment.removeComment(comment.getObjectId());
                momentStore.save(moment);
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
        List<MomentComment> commentList = getData();
        for (Iterator<MomentComment> it = commentList.iterator(); it.hasNext();) {
            MomentComment comment = it.next();
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

    private void reportComment(MomentComment comment) {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_MOMENT_COMMENT);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, comment.getObjectId());
        startActivity(intent);
    }
}
