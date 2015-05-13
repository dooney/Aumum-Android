package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.MomentStore;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.ChatService;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.chat.ChatActivity;
import com.aumum.app.mobile.ui.album.AlbumAdapter;
import com.aumum.app.mobile.ui.moment.MomentDetailsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.EditTextDialog;
import com.aumum.app.mobile.ui.view.dialog.TextViewDialog;
import com.aumum.app.mobile.ui.view.pulltorefresh.XGridView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.etsy.android.grid.StaggeredGridView;
import com.github.kevinsawicki.wishlist.Toaster;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserFragment extends LoaderFragment<User> {

    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject ChatService chatService;
    @Inject RestService restService;
    @Inject FileUploadService fileUploadService;

    private String userId;
    private String screenName;
    private User currentUser;
    private User user;
    private List<String> album;
    private List<Moment> momentList;

    private View mainView;
    private AlbumAdapter albumAdapter;
    private ImageView coverImage;
    private AvatarImageView avatarImage;
    private TextView cityText;
    private TextView areaText;
    private TextView aboutText;
    private ViewGroup actionLayout;
    private View addContactButton;
    private View chatButton;
    private View deleteContactButton;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainView = view.findViewById(R.id.main_view);
        actionLayout = (ViewGroup) view.findViewById(R.id.layout_action);
        addContactButton = view.findViewById(R.id.b_add_contact);
        chatButton = view.findViewById(R.id.b_chat);
        deleteContactButton = view.findViewById(R.id.b_delete_contact);

        XGridView userView = (XGridView) view.findViewById(R.id.user_view);
        userView.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
        StaggeredGridView staggeredView = userView.getRefreshableView();
        View header = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_user_header, null, false);
        staggeredView.addHeaderView(header);
        coverImage = (ImageView) header.findViewById(R.id.image_cover);
        avatarImage = (AvatarImageView) header.findViewById(R.id.image_avatar);
        cityText = (TextView) header.findViewById(R.id.text_city);
        areaText = (TextView) header.findViewById(R.id.text_area);
        aboutText = (TextView) header.findViewById(R.id.text_about);

        albumAdapter = new AlbumAdapter(getActivity());
        userView.setAdapter(albumAdapter);
        userView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Moment moment = momentList.get(i - 1);
                final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID, moment.getObjectId());
                startActivity(intent);
            }
        });
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
        List<Moment> moments = momentStore.loadMore(user.getMoments(), null);
        for (Moment moment: moments) {
            album.add(fileUploadService.getThumbnail(moment.getImageUrl()));
            momentList.add(moment);
        }
        return user;
    }

    @Override
    protected void handleLoadResult(final User user) {
        if (user != null) {
            setData(user);

            getActivity().setTitle(user.getScreenName());
            albumAdapter.addAll(album);
            if (user.getCoverUrl() != null) {
                ImageLoaderUtils.displayImage(user.getCoverUrl(), coverImage);
            } else {
                coverImage.setImageResource(
                        Constants.Options.CITY_COVER.get(user.getCity()));
            }
            avatarImage.getFromUrl(user.getAvatarUrl());
            cityText.setText(user.getCity());
            areaText.setText(user.getArea());
            aboutText.setText(user.getAbout());
            if (currentUser.getObjectId().equals(userId)) {
                actionLayout.setVisibility(View.GONE);
                return;
            }
            boolean isContact = currentUser.isContact(userId);
            toggleActionButton(isContact);
            if (isContact) {
                chatButton.setOnClickListener(new View.OnClickListener() {
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
                                        toggleActionButton(false);
                                    }
                                }).show();
                    }
                });
            } else {
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

    private void toggleActionButton(boolean isContact) {
        if (isContact) {
            addContactButton.setVisibility(View.GONE);
            deleteContactButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.VISIBLE);
        } else {
            addContactButton.setVisibility(View.VISIBLE);
            deleteContactButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.GONE);
        }
    }
}
