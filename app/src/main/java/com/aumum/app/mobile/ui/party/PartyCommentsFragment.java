package com.aumum.app.mobile.ui.party;

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
import com.aumum.app.mobile.core.infra.security.ApiKeyProvider;
import com.aumum.app.mobile.core.model.Message;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.MessageDeliveryService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.model.Comment;
import com.aumum.app.mobile.core.dao.PartyCommentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.comment.CommentsAdapter;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.comment.DeleteCommentListener;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.QuickReturnListView;
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
public class PartyCommentsFragment extends ItemListFragment<Comment>
        implements DeleteCommentListener.OnActionListener,
                   QuickReturnListView.OnScrollDirectionListener {
    private String partyId;
    private User currentUser;
    private Party party;
    private PartyCommentStore partyCommentStore;

    private SafeAsyncTask<Boolean> task;
    private Comment repliedComment;

    @Inject RestService service;
    @Inject MessageDeliveryService messageDeliveryService;
    @Inject ApiKeyProvider apiKeyProvider;
    @Inject UserStore userStore;
    @Inject PartyStore partyStore;

    private QuickReturnListView quickReturnListView;
    private ViewGroup layoutAction;
    private ViewGroup layoutCommentBox;
    private TextView commentText;
    private boolean isCommentBoxShow;
    private EditText editComment;
    private ImageView postCommentButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        partyCommentStore = new PartyCommentStore();
        final Intent intent = getActivity().getIntent();
        partyId = intent.getStringExtra(PartyCommentsActivity.INTENT_PARTY_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party_comments, null);

        layoutAction = (ViewGroup) view.findViewById(R.id.layout_action);
        layoutCommentBox = (ViewGroup) view.findViewById(R.id.layout_comment_box);

        commentText = (TextView) view.findViewById(R.id.text_comment);
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCommentBox();
                repliedComment = null;
            }
        });

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

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quickReturnListView = (QuickReturnListView) getListView();
        quickReturnListView.setOnScrollDirectionListener(this);
        quickReturnListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showCommentBox();
                repliedComment = getData().get(i);
                editComment.setHint(getString(R.string.hint_reply_comment,
                        repliedComment.getUser().getScreenName(), repliedComment.getContent()));
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
        party = partyStore.getPartyById(partyId);
        return partyCommentStore.getPartyComments(partyId);
    }

    @Override
    protected void handleLoadResult(List<Comment> result) {
        try {
            if (result != null) {
                for (Comment comment : result) {
                    comment.setUser(userStore.getUserById(comment.getUserId()));
                }
                getData().addAll(result);
                getListAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    @Override
    protected ArrayAdapter<Comment> createAdapter(List<Comment> items) {
        String currentUserId = apiKeyProvider.getAuthUserId();
        return new CommentsAdapter(getActivity(), items, currentUserId, this);
    }

    private void toggleCommentBox() {
        if (isCommentBoxShow) {
            hideCommentBox();
        } else {
            editComment.setHint(R.string.hint_new_comment);
            showCommentBox();
        }
        isCommentBoxShow = !isCommentBoxShow;
    }

    private void hideCommentBox() {
        EditTextUtils.hideSoftInput(editComment);
        editComment.setText(null);
        Animation.flyOut(layoutCommentBox);
    }

    private void showCommentBox() {
        Animation.flyIn(layoutCommentBox);
        EditTextUtils.showSoftInput(editComment, true);
    }

    private void enableSubmit() {
        postCommentButton.setEnabled(true);
    }

    private void disableSubmit() {
        postCommentButton.setEnabled(false);
    }

    private void submitComment() {
        if (task != null) {
            return;
        }

        // update UI first
        Comment comment = new Comment();
        comment.setParentId(partyId);
        String content = editComment.getText().toString();
        if (repliedComment != null) {
            comment.setRepliedId(repliedComment.getObjectId());
            comment.setContent(getString(R.string.hint_reply_comment,
                    repliedComment.getUser().getScreenName(), content));
        } else {
            comment.setContent(content);
        }
        comment.setUserId(currentUser.getObjectId());
        comment.setUser(currentUser);
        getData().add(0, comment);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();
        hideCommentBox();
        disableSubmit();

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
                getListAdapter().notifyDataSetChanged();
                show();
                enableSubmit();
            }
        };
        task.execute();
    }

    @Override
    public void onCommentDeletedSuccess(String commentId) {
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
        Toaster.showLong(getActivity(), R.string.error_delete_comment);
    }

    @Override
    public void onScrollUp() {
        if (!isCommentBoxShow) {
            Animation.animateIconBar(layoutAction, true);
        }
    }

    @Override
    public void onScrollDown() {
        if (isCommentBoxShow) {
            return;
        }

        QuickReturnListView listView = (QuickReturnListView) getListView();
        boolean canScrollDown = listView.canScrollDown();
        boolean canScrollUp = listView.canScrollUp();
        if (!canScrollDown) {
            Animation.animateIconBar(layoutAction, true);
        } else if (canScrollDown && canScrollUp) {
            Animation.animateIconBar(layoutAction, false);
        }
    }
}
