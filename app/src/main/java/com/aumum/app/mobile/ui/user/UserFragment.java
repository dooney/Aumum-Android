package com.aumum.app.mobile.ui.user;

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
import android.widget.ImageView;
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
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.Arrays;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {

    @Inject UserStore userStore;
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
        MenuItem more = menu.add(Menu.NONE, 0, Menu.NONE, null);
        more.setActionView(R.layout.menuitem_more);
        more.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View moreView = more.getActionView();
        ImageView moreIcon = (ImageView) moreView.findViewById(R.id.b_more);
        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    showActionDialog();
                }
            }
        });
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
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return mainView;
    }

    @Override
    protected User loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        if (userId != null) {
            if (userId.equals(currentUser.getObjectId())) {
                return currentUser;
            }
            user = userStore.getUserByIdFromServer(userId);
        } else if (screenName != null) {
            user = userStore.getUserByScreenNameFromServer(screenName);
            userId = user.getObjectId();
        }
        return user;
    }

    @Override
    protected void handleLoadResult(final User user) {
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
            if (currentUser.isContact(userId)) {
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
                                        currentUser.removeContact(userId);
                                        userStore.save(currentUser);
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
                                }).show();
                    }
                });
            }
        }
    }

    private void showActionDialog() {
        String options[] = getResources().getStringArray(R.array.label_user_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
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
        }).show();
    }

    private void startHerPartiesActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE,
                getString(R.string.title_activity_user_parties, user.getScreenName()));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, userId);
        startActivity(intent);
    }

    private void startHerAskingsActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchAskingActivity.class);
        intent.putExtra(SearchAskingActivity.INTENT_TITLE,
                getString(R.string.title_activity_user_askings, user.getScreenName()));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, userId);
        startActivity(intent);
    }
}
