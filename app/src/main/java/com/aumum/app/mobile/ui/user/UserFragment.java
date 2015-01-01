package com.aumum.app.mobile.ui.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.asking.SearchAskingActivity;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.party.SearchPartyActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.aumum.app.mobile.utils.DialogUtils;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {

    @Inject UserStore dataStore;
    @Inject ChatService chatService;
    @Inject RestService restService;

    private String userId;
    private String screenName;
    private User currentUser;
    private User user;

    private View mainView;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView cityText;
    private TextView areaText;
    private TextView aboutText;
    private Button addContactButton;
    private ViewGroup actionLayout;
    private Button sendMessageButton;
    private Button deleteContactButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        menu.add(Menu.NONE, 0, Menu.NONE, "MORE")
                .setIcon(R.drawable.ic_fa_ellipsis_v)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!isUsable()) {
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                showActionDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        cityText = (TextView) view.findViewById(R.id.text_city);
        areaText = (TextView) view.findViewById(R.id.text_area);
        aboutText = (TextView) view.findViewById(R.id.text_about);
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
                cityText.setText(user.getCity());
                areaText.setText(user.getArea());
                aboutText.setText(user.getAbout());
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
                    deleteContactButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new TextViewDialog(getActivity(), getString(R.string.info_confirm_delete_contact),
                                    new ConfirmDialog.OnConfirmListener() {
                                @Override
                                public void call(Object value) throws Exception {
                                    chatService.deleteConversation(user.getChatId());
                                    chatService.deleteContact(userId);
                                    String currentUserId = currentUser.getObjectId();
                                    restService.removeContact(currentUserId, userId);
                                    dataStore.removeContact(currentUserId, userId);
                                    restService.removeContact(userId, currentUserId);
                                    dataStore.removeContact(userId, currentUserId);
                                }

                                @Override
                                public void onException(String errorMessage) {
                                    Toaster.showShort(getActivity(), errorMessage);
                                }

                                @Override
                                public void onSuccess(Object value) {
                                    actionLayout.setVisibility(View.GONE);
                                    addContactButton.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFailed() {
                                    Toaster.showShort(getActivity(), R.string.error_delete_contact);
                                }
                            }).show();
                        }
                    });
                } else {
                    addContactButton.setVisibility(View.VISIBLE);
                    addContactButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new EditTextDialog(getActivity(), R.layout.dialog_edit_text_multiline, R.string.hint_hello,
                                    new ConfirmDialog.OnConfirmListener() {
                                        @Override
                                        public void call(Object value) throws Exception {
                                            String hello = (String) value;
                                            chatService.addContact(userId, hello);
                                            Thread.sleep(1000);
                                        }

                                        @Override
                                        public void onException(String errorMessage) {
                                            Toaster.showShort(getActivity(), errorMessage);
                                        }

                                        @Override
                                        public void onSuccess(Object value) {
                                            Toaster.showShort(getActivity(), R.string.info_add_contact_sent);
                                        }

                                        @Override
                                        public void onFailed() {
                                            Toaster.showShort(getActivity(), R.string.error_add_contact);
                                        }
                                    }).show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            Ln.d(e);
        }
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_user_actions);
        DialogUtils.showDialog(getActivity(), options,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                startHerPartiesActivity(user);
                                break;
                            case 1:
                                startHerAskingsActivity(user);
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void startHerPartiesActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE,
                getString(R.string.title_user_parties, user.getScreenName()));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, userId);
        startActivity(intent);
    }

    private void startHerAskingsActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE,
                getString(R.string.title_user_askings, user.getScreenName()));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, userId);
        startActivity(intent);
    }
}
