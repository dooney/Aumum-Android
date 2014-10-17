package com.aumum.app.mobile.ui.party;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.RestService;
import com.aumum.app.mobile.core.Comment;
import com.aumum.app.mobile.core.PartyCommentStore;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.ui.comment.CommentsAdapter;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.CommentTextView;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;

import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyCommentsFragment extends ItemListFragment<Comment> {
    private String partyId;
    private User currentUser;
    private PartyCommentStore partyCommentStore;
    private UserStore userStore;

    private SafeAsyncTask<Boolean> task;

    @Inject
    RestService service;

    private ViewGroup layoutCommentBox;
    private CommentTextView commentText;
    private boolean isCommentBoxShow;
    private EditText editComment;
    private ImageView postCommentButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        partyCommentStore = new PartyCommentStore();
        userStore = UserStore.getInstance(null);
        final Intent intent = getActivity().getIntent();
        partyId = intent.getStringExtra(PartyCommentsActivity.INTENT_PARTY_ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_party_comments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party_comments, null);

        layoutCommentBox = (ViewGroup) view.findViewById(R.id.layout_comment_box);

        commentText = (CommentTextView) view.findViewById(R.id.text_comment);
        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCommentBox();
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
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_party_comments;
    }

    @Override
    protected List<Comment> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser(false);
        return partyCommentStore.getPartyComments(partyId);
    }

    @Override
    protected void handleLoadResult(List<Comment> result) throws Exception {
        if (result != null) {
            for (Comment comment : result) {
                comment.setUser(userStore.getUserById(comment.getUserId(), false));
            }
            getData().addAll(result);
            getListAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected ArrayAdapter<Comment> createAdapter(List<Comment> items) {
        return new CommentsAdapter(getActivity(), items);
    }

    private void toggleCommentBox() {
        if (isCommentBoxShow) {
            hideCommentBox();
        } else {
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
        Comment partyComment = new Comment();
        partyComment.setParentId(partyId);
        partyComment.setText(editComment.getText().toString());
        partyComment.setUserId(currentUser.getObjectId());
        partyComment.setUser(currentUser);
        getData().add(0, partyComment);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToTop();
        hideCommentBox();
        disableSubmit();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                Comment comment = getData().get(0);
                final Comment newComment = new Comment();
                newComment.setParentId(comment.getParentId());
                newComment.setText(comment.getText());
                newComment.setUserId(comment.getUserId());
                Comment response = service.newPartyComment(newComment);
                service.addPartyComment(partyId, response.getObjectId());
                comment.setObjectId(response.getObjectId());
                comment.setCreatedAt(response.getCreatedAt());
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
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
}
