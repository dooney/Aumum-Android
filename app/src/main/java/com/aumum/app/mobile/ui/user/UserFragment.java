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
import android.widget.AdapterView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.dao.MessageStore;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.album.AlbumAdapter;
import com.aumum.app.mobile.ui.moment.MomentDetailsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.paginggrid.PagingGridView;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.EditTextDialog;
import com.aumum.app.mobile.ui.view.dialog.TextViewDialog;
import com.aumum.app.mobile.utils.EMChatUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {

    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject MessageStore messageStore;
    @Inject RestService restService;
    @Inject FileUploadService fileUploadService;

    private String userId;
    private String screenName;
    private User currentUser;
    private User user;
    private List<String> album;
    private List<Moment> momentList;
    private int photoCount;
    private SafeAsyncTask<Boolean> task;

    private PagingGridView gridView;
    private AlbumAdapter albumAdapter;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView addressText;
    private TextView aboutText;
    private View addContactButton;
    private View chatButton;
    private MenuItem deleteContact;
    private TextView photoCountText;
    private TextView contactCountText;
    private View contactLayout;

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
        album = new ArrayList<>();
        momentList = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        deleteContact = menu.add(Menu.NONE, 0, Menu.NONE, null);
        deleteContact.setVisible(false);
        deleteContact.setActionView(R.layout.menuitem_delete_contact);
        deleteContact.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View usersView = deleteContact.getActionView();
        View button = usersView.findViewById(R.id.b_delete_contact);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    new TextViewDialog(getActivity(), getString(R.string.info_confirm_delete_contact),
                            new ConfirmDialog.OnConfirmListener() {
                                @Override
                                public void call(Object value) throws Exception {
                                    EMChatUtils.deleteConversation(user.getChatId());
                                    EMChatUtils.deleteContact(userId);
                                    String currentUserId = currentUser.getObjectId();
                                    restService.removeContact(currentUserId, userId);
                                    currentUser.removeContact(userId);
                                    messageStore.deleteContactRequest(userId);
                                    userStore.save(currentUser);
                                }

                                @Override
                                public void onException(String errorMessage) {
                                    Toaster.showShort(getActivity(), errorMessage);
                                }

                                @Override
                                public void onSuccess(Object value) {
                                    toggleActionButton(false);
                                }
                            }).show();
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

        View header = getActivity().getLayoutInflater()
                .inflate(R.layout.grid_user_header, null);
        initHeaderView(header);
        albumAdapter = new AlbumAdapter(getActivity());
        gridView = (PagingGridView) view.findViewById(R.id.grid_view);
        gridView.addHeaderView(header, null, false);
        gridView.setAdapter(albumAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Moment moment = momentList.get(i - gridView.getNumColumns());
                final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID, moment.getObjectId());
                startActivity(intent);
            }
        });
        gridView.setPagingListener(new PagingGridView.Paging() {
            @Override
            public void onLoadMoreItems() {
                if (task != null) {
                    return;
                }
                task = new SafeAsyncTask<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        if (momentList.size() > 0) {
                            Moment last = momentList.get(momentList.size() - 1);
                            List<Moment> moments = momentStore.getUserMoments(
                                    user.getObjectId(), last.getCreatedAt());
                            final int count = moments.size();
                            if (count > 0) {
                                for (Moment moment : moments) {
                                    album.add(fileUploadService.getThumbnail(
                                            moment.getImageUrl()));
                                    momentList.add(moment);
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!momentStore.isFullLoad(count)) {
                                            gridView.onFinishLoading(false, null);
                                        }
                                        albumAdapter.notifyDataSetChanged();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        gridView.onFinishLoading(false, null);
                                    }
                                });
                            }
                        }
                        return true;
                    }

                    @Override
                    protected void onException(Exception e) throws RuntimeException {
                        if (!(e instanceof RetrofitError)) {
                            showError(e);
                        }
                    }

                    @Override
                    protected void onFinally() throws RuntimeException {
                        task = null;
                    }
                };
                task.execute();
            }
        });
    }

    private void initHeaderView(View view) {
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        addressText = (TextView) view.findViewById(R.id.text_address);
        aboutText = (TextView) view.findViewById(R.id.text_about);
        addContactButton = view.findViewById(R.id.b_add_contact);
        chatButton = view.findViewById(R.id.b_chat);
        photoCountText = (TextView) view.findViewById(R.id.text_photo_count);
        contactCountText = (TextView) view.findViewById(R.id.text_contact_count);
        contactLayout = view.findViewById(R.id.layout_contact);
    }

    @Override
    protected boolean readyToShow() {
        return getData() != null;
    }

    @Override
    protected View getMainView() {
        return gridView;
    }

    @Override
    protected User loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        if (userId != null) {
            if (userId.equals(currentUser.getObjectId())) {
                user = currentUser;
            } else {
                user = userStore.getUserByIdFromServer(userId);
            }
        } else if (screenName != null) {
            user = userStore.getUserByScreenNameFromServer(screenName);
            userId = user.getObjectId();
        }
        photoCount = momentStore.getUserMomentsCount(userId);
        List<Moment> moments = momentStore.getUserMoments(userId, null);
        if (moments.size() > 0) {
            album.clear();
            momentList.clear();
            for (Moment moment : moments) {
                album.add(fileUploadService.getThumbnail(moment.getImageUrl()));
                momentList.add(moment);
            }
        }
        return user;
    }

    @Override
    protected void handleLoadResult(final User user) {
        if (user != null) {
            setData(user);

            gridView.setHasMoreItems(momentStore.isFullLoad(album.size()));
            albumAdapter.addAll(album);
            avatarImage.getFromUrl(user.getAvatarUrl());
            screenNameText.setText(user.getScreenName());
            addressText.setText(user.getAddress());
            if (user.getAbout() != null) {
                aboutText.setText(user.getAbout());
                aboutText.setVisibility(View.VISIBLE);
            }
            photoCountText.setText(String.valueOf(photoCount));
            contactCountText.setText(String.valueOf(user.getContacts().size()));
            contactLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(
                            getActivity(), UserContactsActivity.class);
                    intent.putExtra(UserContactsActivity.INTENT_TITLE,
                            getString(R.string.label_user_contacts, user.getScreenName()));
                    intent.putStringArrayListExtra(UserContactsActivity.INTENT_CONTACTS,
                            user.getContacts());
                    startActivity(intent);
                }
            });
            if (!currentUser.getObjectId().equals(userId)) {
                boolean isContact = currentUser.isContact(userId);
                toggleActionButton(isContact);
                if (isContact) {
                    chatButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra(ChatActivity.INTENT_TITLE, user.getScreenName());
                            intent.putExtra(ChatActivity.INTENT_ID, user.getChatId());
                            startActivity(intent);
                        }
                    });
                } else {
                    addContactButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditTextDialog dialog = new EditTextDialog(getActivity(),
                                    R.layout.dialog_edit_text_multiline,
                                    R.string.hint_hello,
                                    new ConfirmDialog.OnConfirmListener() {
                                        @Override
                                        public void call(Object value) throws Exception {
                                            String hello = (String) value;
                                            EMChatUtils.addContact(userId, hello);
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
                                    });
                            dialog.setText(getString(R.string.label_hello, currentUser.getScreenName()));
                            dialog.show();
                        }
                    });
                }
            }
        }
    }

    private void toggleActionButton(boolean isContact) {
        if (isContact) {
            addContactButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.VISIBLE);
            deleteContact.setVisible(true);
        } else {
            addContactButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.GONE);
            deleteContact.setVisible(false);
        }
    }
}
