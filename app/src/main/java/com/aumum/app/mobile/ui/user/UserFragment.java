package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.ui.asking.SearchAskingActivity;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.contact.AddContactActivity;
import com.aumum.app.mobile.ui.contact.DeleteContactListener;
import com.aumum.app.mobile.ui.party.SearchPartyActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
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
    private String screenName;
    private User currentUser;

    private View mainView;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView areaText;
    private TextView aboutText;
    private ViewGroup partiesLayout;
    private ViewGroup askingsLayout;
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
        Uri data = intent.getData();
        if (data != null) {
            String d = data.toString();
            int index = d.lastIndexOf("@");
            screenName = d.substring(index + 1);
        }
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
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        areaText = (TextView) view.findViewById(R.id.text_area);
        aboutText = (TextView) view.findViewById(R.id.text_about);
        partiesLayout = (ViewGroup) view.findViewById(R.id.layout_her_parties);
        askingsLayout = (ViewGroup) view.findViewById(R.id.layout_her_askings);
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
        User user = null;
        if (userId != null) {
            if (userId.equals(currentUser.getObjectId())) {
                return currentUser;
            }
            user = dataStore.getUserByIdFromServer(userId);
        } else if (screenName != null) {
            user = dataStore.getUserByScreenNameFromServer(screenName);
            userId = user.getObjectId();
        }
        if (user == null) {
            throw new Exception(getString(R.string.error_load_user));
        }
        return user;
    }

    @Override
    protected void handleLoadResult(final User user) {
        try {
            if (user != null) {
                setData(user);

                avatarImage.getFromUrl(user.getAvatarUrl());
                screenNameText.setText(user.getScreenName());
                areaText.setText(Constants.Options.AREA_OPTIONS[user.getArea()]);
                aboutText.setText(user.getAbout());
                partiesLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
                        intent.putExtra(SearchPartyActivity.INTENT_TITLE,
                                getString(R.string.title_user_parties, user.getScreenName()));
                        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, userId);
                        startActivity(intent);
                    }
                });
                askingsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
                        intent.putExtra(SearchAskingActivity.INTENT_TITLE,
                                getString(R.string.title_user_askings, user.getScreenName()));
                        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, userId);
                        startActivity(intent);
                    }
                });
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
                            intent.putExtra(ChatActivity.INTENT_ID, user.getChatId());
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
        Toaster.showShort(getActivity(), R.string.error_delete_contact);
    }
}
