package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.contact.AddContactActivity;
import com.aumum.app.mobile.ui.contact.DeleteContactListener;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User>
        implements DeleteContactListener.OnActionListener {
    @Inject UserStore dataStore;

    private String userId;
    private User currentUser;

    private View mainView;
    private Button addContactButton;
    private ViewGroup actionLayout;
    private Button sendMessageButton;
    private Button deleteContactButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        final Intent intent = getActivity().getIntent();
        userId = intent.getStringExtra(UserActivity.INTENT_USER_ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.error_load_user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view.findViewById(R.id.main_view);
        addContactButton = (Button) view.findViewById(R.id.b_add_contact);
        actionLayout = (ViewGroup) view.findViewById(R.id.layout_action);
        sendMessageButton = (Button) view.findViewById(R.id.b_send_message);
        deleteContactButton = (Button) view.findViewById(R.id.b_delete_contact);
    }

    @Override
    public void onDestroyView() {
        mainView = null;

        super.onDestroyView();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_user;
    }

    @Override
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected User loadDataCore(Bundle bundle) throws Exception {
        currentUser = dataStore.getCurrentUser();
        if (userId.equals(currentUser.getObjectId())) {
            return currentUser;
        } else {
            User user = dataStore.getUserByIdFromServer(userId);
            if (user == null) {
                throw new Exception(getString(R.string.error_load_user));
            }
            return user;
        }
    }

    @Override
    protected void handleLoadResult(final User user) {
        try {
            if (user != null) {
                setData(user);

                addContactButton.setVisibility(View.GONE);
                actionLayout.setVisibility(View.GONE);
                if (currentUser.getObjectId().equals(userId)) {
                    return;
                }
                if (currentUser.getContacts().contains(userId)) {
                    actionLayout.setVisibility(View.VISIBLE);
                    sendMessageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra(ChatActivity.INTENT_TITLE, user.getScreenName());
                            intent.putExtra(ChatActivity.INTENT_TYPE, ChatActivity.TYPE_SINGLE);
                            intent.putExtra(ChatActivity.INTENT_ID, userId);
                            startActivity(intent);
                        }
                    });
                    DeleteContactListener deleteContactListener = new DeleteContactListener(getActivity(), userId);
                    deleteContactListener.setOnProgressListener((DeleteContactListener.OnProgressListener)getActivity());
                    deleteContactListener.setOnActionListener(this);
                    deleteContactButton.setOnClickListener(deleteContactListener);
                } else {
                    addContactButton.setVisibility(View.VISIBLE);
                    addContactButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Intent intent = new Intent(getActivity(), AddContactActivity.class);
                            intent.putExtra(AddContactActivity.INTENT_TO_USER_ID, userId);
                            intent.putExtra(AddContactActivity.INTENT_FROM_USER_NAME, currentUser.getScreenName());
                            startActivity(intent);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    @Override
    public void onDeleteContactSuccess(String contactId) {
        actionLayout.setVisibility(View.GONE);
        addContactButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleteContactFailed() {
        Toaster.showLong(getActivity(), R.string.error_delete_contact);
    }
}
