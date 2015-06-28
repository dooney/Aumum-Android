package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.ui.album.AlbumAdapter;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.contact.ContactActivity;
import com.aumum.app.mobile.ui.moment.MomentDetailsActivity;
import com.aumum.app.mobile.ui.settings.SettingsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.paginggrid.PagingGridView;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends LoaderFragment<User> {

    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject FileUploadService fileUploadService;

    private User currentUser;
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
    private View editProfileButton;
    private TextView photoCountText;
    private TextView contactCountText;
    private View contactLayout;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        album = new ArrayList<>();
        momentList = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        MenuItem settings = menu.add(Menu.NONE, 0, Menu.NONE, null);
        settings.setActionView(R.layout.menuitem_settings);
        settings.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View settingsView = settings.getActionView();
        ImageView settingsIcon = (ImageView) settingsView.findViewById(R.id.b_settings);
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    final Intent intent = new Intent(getActivity(),
                            SettingsActivity.class);
                    getActivity().startActivityForResult(intent,
                            Constants.RequestCode.SETTINGS_REQ_CODE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View header = getActivity().getLayoutInflater()
                .inflate(R.layout.grid_profile_header, null);
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
                                    currentUser.getObjectId(), last.getCreatedAt());
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
        editProfileButton = view.findViewById(R.id.b_edit_profile);
        photoCountText = (TextView) view.findViewById(R.id.text_photo_count);
        contactCountText = (TextView) view.findViewById(R.id.text_contact_count);
        contactLayout = view.findViewById(R.id.layout_contact);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.EDIT_PROFILE_REQ_CODE) {
            try {
                currentUser = userStore.getCurrentUser();
                updateProfile(currentUser);
            } catch (Exception e) {
                showError(e);
            }
        }
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
        photoCount = momentStore.getUserMomentsCount(currentUser.getObjectId());
        List<Moment> moments = momentStore.getUserMoments(currentUser.getObjectId(), null);
        if (moments.size() > 0) {
            album.clear();
            momentList.clear();
            for (Moment moment : moments) {
                album.add(fileUploadService.getThumbnail(moment.getImageUrl()));
                momentList.add(moment);
            }
        }
        return currentUser;
    }

    @Override
    protected void handleLoadResult(final User user) {
        if (user != null) {
            setData(user);

            gridView.setHasMoreItems(momentStore.isFullLoad(album.size()));
            albumAdapter.addAll(album);
            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    Gson gson = new Gson();
                    String data = gson.toJson(user);
                    intent.putExtra(EditProfileActivity.INTENT_CURRENT_USER, data);
                    startActivityForResult(intent, Constants.RequestCode.EDIT_PROFILE_REQ_CODE);
                }
            });
            updateProfile(user);
            photoCountText.setText(String.valueOf(photoCount));
            contactCountText.setText(String.valueOf(currentUser.getContacts().size()));
            contactLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(getActivity(), ContactActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void updateProfile(User user) {
        avatarImage.getFromUrl(user.getAvatarUrl());
        screenNameText.setText(user.getScreenName());
        addressText.setText(user.getAddress());
        if (user.getAbout() != null) {
            aboutText.setText(user.getAbout());
            aboutText.setVisibility(View.VISIBLE);
        }
    }
}