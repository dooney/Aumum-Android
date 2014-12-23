package com.aumum.app.mobile.ui.party;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.dao.PartyCommentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.comment.CommentCard;
import com.aumum.app.mobile.ui.comment.CommentsAdapter;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyCommentsFragment extends ItemListFragment<Comment> {
    @Inject RestService service;
    @Inject MessageDeliveryService messageDeliveryService;
    @Inject UserStore userStore;
    @Inject PartyStore partyStore;
    @Inject PartyCommentStore partyCommentStore;

    private String partyId;
    private User currentUser;
    private Party party;

    private SafeAsyncTask<Boolean> task;
    private Comment repliedComment;

    private EditText editComment;
    private ImageView postCommentButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        partyId = intent.getStringExtra(PartyCommentsActivity.INTENT_PARTY_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party_comments, null);

        editComment = (EditText) view.findViewById(R.id.edit_comment);
        editComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND)
                    submitComment();
                return false;
            }
        });

        postCommentButton = (ImageView) view.findViewById(R.id.image_post_comment);
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitComment();
            }
        });
        disableSubmit();

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Comment comment = getData().get(position);
                if (party.isOwner(currentUser.getObjectId()) ||
                        comment.isOwner(currentUser.getObjectId())) {
                    showActionDialog(view);
                } else {
                    reply(comment);
                }
            }
        });
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_party_comments;
    }

    @Override
    protected List<Comment> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        party = partyStore.getPartyByIdFromServer(partyId);
        List<Comment> result = partyCommentStore.getPartyComments(party.getComments());
        for (Comment comment : result) {
            comment.setUser(userStore.getUserById(comment.getUserId()));
        }
        return result;
    }

    @Override
    protected ArrayAdapter<Comment> createAdapter(List<Comment> items) {
        return new CommentsAdapter(getActivity(), items);
    }

    @Override
    protected void handleLoadResult(List<Comment> result) {
        super.handleLoadResult(result);
        enableSubmit();
    }

    private void enableSubmit() {
        postCommentButton.setEnabled(true);
    }

    private void disableSubmit() {
        postCommentButton.setEnabled(false);
    }

    private void resetCommentBox() {
        EditTextUtils.hideSoftInput(editComment);
        editComment.clearFocus();
        editComment.setText(null);
        editComment.setHint(R.string.hint_new_comment);
    }

    private void submitComment() {
        if (task != null) {
            return;
        }

        // update UI first
        String repliedId = null;
        String content = editComment.getText().toString();
        if (repliedComment != null) {
            repliedId = repliedComment.getObjectId();
            content = getString(R.string.hint_reply_comment,
                    repliedComment.getUser().getScreenName(), content);
        }
        Comment comment = new Comment(partyId, repliedId, content, currentUser.getObjectId());
        comment.setUser(currentUser);
        getData().add(0, comment);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();
        disableSubmit();
        resetCommentBox();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                Comment comment = getData().get(0);
                final Comment newComment = new Comment(
                        comment.getParentId(),
                        comment.getRepliedId(),
                        comment.getContent(),
                        comment.getUserId());
                Comment response = service.newPartyComment(newComment);
                service.addPartyComment(partyId, response.getObjectId());
                comment.setObjectId(response.getObjectId());
                comment.setCreatedAt(response.getCreatedAt());
                party.getComments().add(response.getObjectId());
                partyStore.updateOrInsert(party);

                Message message = new Message(Message.Type.PARTY_COMMENT,
                        currentUser.getObjectId(), party.getUserId(), comment.getContent(), party.getObjectId());
                messageDeliveryService.send(message);
                if (repliedComment != null) {
                    Message repliedMessage = new Message(Message.Type.PARTY_REPLY,
                            currentUser.getObjectId(), repliedComment.getUserId(), comment.getContent(), party.getObjectId());
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
                repliedComment = null;
                getListAdapter().notifyDataSetChanged();
                show();
                enableSubmit();
            }
        };
        task.execute();
    }

    private void reply(Comment comment) {
        EditTextUtils.showSoftInput(editComment, true);
        repliedComment = comment;
        editComment.setHint(getString(R.string.hint_reply_comment,
                repliedComment.getUser().getScreenName(), repliedComment.getContent()));
    }

    private void deleteComment(final CommentCard card) {
        if (task != null) {
            return;
        }
        final Comment comment = card.getComment();
        card.onActionStart();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                service.deletePartyComment(comment.getObjectId());
                service.removePartyComment(comment.getParentId(), comment.getObjectId());
                party.getComments().remove(comment.getObjectId());
                partyStore.updateOrInsert(party);
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
                Toaster.showShort(getActivity(), R.string.error_delete_comment);
            }

            @Override
            public void onSuccess(final Boolean success) {
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

    private void showActionDialog(View view) {
        final CommentCard card = (CommentCard) view.getTag();
        final String options[] = getResources().getStringArray(R.array.label_comment_actions);
        DialogUtils.showDialog(getActivity(), options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        reply(card.getComment());
                        break;
                    case 1:
                        break;
                    case 2:
                        deleteComment(card);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void onCommentDeletedSuccess(String commentId) {
        try {
            List<Comment> commentList = getData();
            for (Iterator<Comment> it = commentList.iterator(); it.hasNext();) {
                Comment comment = it.next();
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
        } catch (Exception e) {
            Ln.d(e);
        }
        Toaster.showShort(getActivity(), R.string.error_delete_comment);
    }
}
