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
import com.aumum.app.mobile.core.dao.CreditRuleStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.CreditRule;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.moment.UserMomentsActivity;
import com.aumum.app.mobile.ui.party.SearchPartyActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.ConfirmDialog;
import com.aumum.app.mobile.ui.view.EditTextDialog;
import com.aumum.app.mobile.ui.view.ListViewDialog;
import com.aumum.app.mobile.ui.view.TextViewDialog;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {

    @Inject UserStore userStore;
    @Inject CreditRuleStore creditRuleStore;
    @Inject ChatService chatService;
    @Inject RestService restService;

    private String userId;
    private String screenName;
    private User currentUser;
    private User user;

    private View mainView;
    private AvatarImageView avatarImage;
    private TextView creditText;
    private TextView screenNameText;
    private TextView cityText;
    private TextView areaText;
    private TextView tagsText[];
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
                if (getActivity() != null && user != null) {
                    showActionDialog(user);
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
        creditText = (TextView) view.findViewById(R.id.text_credit);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        cityText = (TextView) view.findViewById(R.id.text_city);
        areaText = (TextView) view.findViewById(R.id.text_area);
        tagsText = new TextView[3];
        tagsText[0] = (TextView) view.findViewById(R.id.text_tag1);
        tagsText[1] = (TextView) view.findViewById(R.id.text_tag2);
        tagsText[2] = (TextView) view.findViewById(R.id.text_tag3);
        aboutText = (TextView) view.findViewById(R.id.text_about);
        addContactButton = (Button) view.findViewById(R.id.b_add_contact);
        actionLayout = (ViewGroup) view.findViewById(R.id.layout_action);
        sendMessageButton = (Button) view.findViewById(R.id.b_send_message);
        deleteContactButton = (Button) view.findViewById(R.id.b_delete_contact);
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
            creditText.setText(getString(R.string.label_user_credit, user.getCredit()));
            screenNameText.setText(user.getScreenName());
            cityText.setText(user.getCity());
            areaText.setText(user.getArea());
            updateTagsUI(user.getTags());
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
                                        updateCredit(currentUser, CreditRule.DELETE_CONTACT);
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

    private void showActionDialog(final User user) {
        String options[] = getResources().getStringArray(R.array.label_user_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                switch (i) {
                    case 1:
                        startUserPartiesActivity(user);
                        break;
                    case 2:
                        startUserMomentsActivity(user);
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private void startUserPartiesActivity(User user) {
        final Intent intent = new Intent(getActivity(), SearchPartyActivity.class);
        intent.putExtra(SearchPartyActivity.INTENT_TITLE,
                getString(R.string.title_activity_user_parties, user.getScreenName()));
        intent.putExtra(SearchPartyActivity.INTENT_USER_ID, user.getObjectId());
        startActivity(intent);
    }

    private void startUserMomentsActivity(User user) {
        final Intent intent = new Intent(getActivity(), UserMomentsActivity.class);
        intent.putExtra(UserMomentsActivity.INTENT_TITLE,
                getString(R.string.title_activity_user_moments, user.getScreenName()));
        intent.putExtra(UserMomentsActivity.INTENT_USER_ID, user.getObjectId());
        startActivity(intent);
    }

    private void updateTagsUI(final List<String> tags) {
        for (int i = 0; i < tagsText.length; i++) {
            tagsText[i].setText("");
            tagsText[i].setVisibility(View.GONE);
        }
        for (int i = 0; i < tags.size(); i++) {
            tagsText[i].setText(tags.get(i));
            tagsText[i].setVisibility(View.VISIBLE);
        }
    }

    private void updateCredit(User currentUser, int seq) throws Exception {
        CreditRule creditRule = creditRuleStore.getCreditRuleBySeq(seq);
        if (creditRule != null) {
            int credit = creditRule.getCredit();
            restService.updateUserCredit(currentUser.getObjectId(), credit);
            currentUser.updateCredit(credit);
            userStore.save(currentUser);
        }
    }
}
