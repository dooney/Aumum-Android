package com.aumum.app.mobile.ui;

import android.app.Activity;
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

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Comment;
import com.aumum.app.mobile.core.PartyCommentStore;
import com.aumum.app.mobile.core.User;
import com.aumum.app.mobile.core.UserStore;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.CommentTextView;
import com.aumum.app.mobile.util.EditTextUtils;
import com.aumum.app.mobile.util.SafeAsyncTask;

import java.util.List;

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

    private ViewGroup layoutCommentBox;
    private CommentTextView commentText;
    private boolean isCommentBoxShow;
    private EditText editComment;
    private ImageView postCommentButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        partyCommentStore = new PartyCommentStore();
        userStore = UserStore.getInstance(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        final Intent intent = activity.getIntent();
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
        currentUser = userStore.getCurrentUser();
        return partyCommentStore.getPartyComments(partyId);
    }

    @Override
    protected void handleLoadResult(List<Comment> result) {
        getListAdapter().notifyDataSetChanged();
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

    private void submitComment() {
        if (task != null) {
            return;
        }

        final Comment partyComment = new Comment();
        partyComment.setText(editComment.getText().toString());
        partyComment.setUser(currentUser);

        hideCommentBox();

        getData().add(partyComment);
        getListAdapter().notifyDataSetChanged();
        show();
        scrollToLastItem();
    }
}
