package com.aumum.app.mobile.ui.party;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.PartyStore;
import com.aumum.app.mobile.core.model.CmdMessage;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.Party;
import com.aumum.app.mobile.core.model.PartyComment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.core.dao.PartyCommentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.ui.base.ItemListFragment;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.report.ReportActivity;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PartyCommentsFragment extends ItemListFragment<PartyComment> {

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject PartyStore partyStore;
    @Inject PartyCommentStore partyCommentStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject ChatService chatService;

    private String partyId;
    private User currentUser;
    private Party party;

    private SafeAsyncTask<Boolean> task;
    private PartyComment repliedComment;

    private EditText editComment;
    private Button postCommentButton;
    private final TextWatcher watcher = validationTextWatcher();

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
        editComment.addTextChangedListener(watcher);

        postCommentButton = (Button) view.findViewById(R.id.b_post_comment);
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
        updateUIWithValidation();
    }

    @Override
    protected List<PartyComment> loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        party = partyStore.getPartyByIdFromServer(partyId);
        if (party.getDeletedAt() != null) {
            throw new Exception(getString(R.string.error_party_was_deleted));
        }
        List<PartyComment> result = partyCommentStore.getPartyComments(party.getComments());
        for (PartyComment comment : result) {
            comment.setUser(userStore.getUserById(comment.getUserId()));
        }
        return result;
    }

    @Override
    protected ArrayAdapter<PartyComment> createAdapter(List<PartyComment> items) {
        return new PartyCommentsAdapter(getActivity(), items);
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(editComment);
        if (postCommentButton != null) {
            postCommentButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void disableSubmit() {
        postCommentButton.setEnabled(false);
    }

    private void resetCommentBox() {
        EditTextUtils.hideSoftInput(editComment);
        editComment.clearFocus();
        editComment.setText(null);
        editComment.setHint(R.string.hint_new_party_comment);
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
            content = getString(R.string.hint_reply_party_comment,
                    repliedComment.getUser().getScreenName(), content);
        }
        PartyComment comment = new PartyComment(partyId, repliedId, content, currentUser.getObjectId());
        comment.setUser(currentUser);
        getData().add(0, comment);
        getListAdapter().notifyDataSetChanged();
        hideEmpty();
        show();
        scrollToTop();
        disableSubmit();
        resetCommentBox();

        // submit
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                PartyComment comment = getData().get(0);
                final PartyComment newComment = new PartyComment(
                        comment.getParentId(),
                        comment.getRepliedId(),
                        comment.getContent(),
                        comment.getUserId());
                PartyComment response = restService.newPartyComment(newComment);
                restService.addPartyComment(partyId, response.getObjectId());
                party.addComment(response.getObjectId());
                partyStore.save(party);
                if (!party.isOwner(currentUser.getObjectId())) {
                    sendCommentMessage(comment);
                    updateCredit(currentUser, CreditRule.ADD_PARTY_COMMENT);
                }
                if (repliedComment != null &&
                    !party.isOwner(repliedComment.getUserId()) &&
                    !repliedComment.isOwner(currentUser.getObjectId())) {
                    sendRepliedMessage(repliedComment);
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
                refresh(null);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
                repliedComment = null;
            }
        };
        task.execute();
    }

    private void reply(PartyComment comment) {
        EditTextUtils.showSoftInput(editComment, true);
        repliedComment = comment;
        editComment.setHint(getString(R.string.hint_reply_party_comment,
                repliedComment.getUser().getScreenName(), repliedComment.getContent()));
    }

    private void deleteComment(final PartyCommentCard card) {
        if (task != null) {
            return;
        }
        final PartyComment comment = card.getComment();
        card.onActionStart();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.deletePartyComment(comment.getObjectId(), comment.getParentId());
                party.removeComment(comment.getObjectId());
                partyStore.save(party);
                updateCredit(currentUser, CreditRule.DELETE_PARTY_COMMENT);
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

    private void reportComment(PartyComment comment) {
        final Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ReportActivity.INTENT_ENTITY_TYPE, ReportActivity.TYPE_PARTY_COMMENT);
        intent.putExtra(ReportActivity.INTENT_ENTITY_ID, comment.getObjectId());
        startActivity(intent);
    }

    private void showActionDialog(View view) {
        final PartyCommentCard card = (PartyCommentCard) view.getTag();
        final PartyComment comment = card.getComment();
        final boolean isOwner = party.isOwner(currentUser.getObjectId()) ||
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

    private void onCommentDeletedSuccess(String commentId) {
        List<PartyComment> commentList = getData();
        for (Iterator<PartyComment> it = commentList.iterator(); it.hasNext();) {
            PartyComment comment = it.next();
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

    private void sendCommentMessage(PartyComment comment) throws Exception {
        String title = getString(R.string.label_comment_party_message,
                currentUser.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.PARTY_COMMENT,
                title, comment.getContent(), partyId);
        User partyOwner = userStore.getUserById(party.getUserId());
        chatService.sendCmdMessage(partyOwner.getChatId(), cmdMessage, false, null);
    }

    private void sendRepliedMessage(PartyComment replied) throws Exception {
        String title = getString(R.string.label_replied_party_message,
                currentUser.getScreenName());
        CmdMessage cmdMessage = new CmdMessage(CmdMessage.Type.PARTY_REPLY,
                title, replied.getContent(), partyId);
        chatService.sendCmdMessage(replied.getUser().getChatId(), cmdMessage, false, null);
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
