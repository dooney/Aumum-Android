package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.album.AlbumAdapter;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.moment.MomentDetailsActivity;
import com.aumum.app.mobile.ui.settings.SettingsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.PagingGridView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.TuSdkUtils;
import com.google.gson.Gson;

import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends LoaderFragment<User>
        implements TuSdkUtils.AlbumListener,
                   TuSdkUtils.CropListener,
                   TuSdkUtils.EditListener,
                   FileUploadService.FileUploadListener {

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject FileUploadService fileUploadService;

    private User currentUser;
    private List<String> album;
    private List<Moment> momentList;
    private SafeAsyncTask<Boolean> task;

    private View mainView;
    private PagingGridView gridView;
    private AlbumAdapter albumAdapter;
    private ImageView coverImage;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView addressText;
    private TextView aboutText;
    private View editProfileButton;

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

        mainView = view.findViewById(R.id.main_view);
        View header = view.findViewById(R.id.header_view);
        initHeaderView(header);

        gridView = (PagingGridView) view.findViewById(R.id.grid_view);
        albumAdapter = new AlbumAdapter(getActivity());
        gridView.setAdapter(albumAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Moment moment = momentList.get(i);
                final Intent intent = new Intent(getActivity(), MomentDetailsActivity.class);
                intent.putExtra(MomentDetailsActivity.INTENT_MOMENT_ID, moment.getObjectId());
                startActivity(intent);
            }
        });
    }

    private void initHeaderView(View view) {
        coverImage = (ImageView) view.findViewById(R.id.image_cover);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        addressText = (TextView) view.findViewById(R.id.text_address);
        aboutText = (TextView) view.findViewById(R.id.text_about);
        editProfileButton = view.findViewById(R.id.b_edit_profile);
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
        return mainView;
    }

    @Override
    protected User loadDataCore(Bundle bundle) throws Exception {
        currentUser = userStore.getCurrentUser();
        List<Moment> moments = momentStore.loadMore(currentUser.getMoments(), null);
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

            albumAdapter.addAll(album);
            if (user.getCoverUrl() != null) {
                ImageLoaderUtils.displayImage(user.getCoverUrl(), coverImage);
            } else {
                coverImage.setImageResource(R.drawable.cover_default);
            }
            coverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TuSdkUtils.album(getActivity(), ProfileFragment.this);
                }
            });
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

    private void updateCover(final String coverUrl) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.updateUserCover(currentUser.getObjectId(), coverUrl);
                currentUser.setCoverUrl(coverUrl);
                userStore.save(currentUser);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
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

    @Override
    public void onUploadSuccess(String remoteUrl) {
        updateCover(remoteUrl);
    }

    @Override
    public void onUploadFailure(Exception e) {
        showError(e);
    }

    private void onPhotoResult(ImageSqlInfo imageSqlInfo) {
        Bitmap bitmap = BitmapHelper.getBitmap(imageSqlInfo);
        if (bitmap.getHeight() > bitmap.getWidth()) {
            TuSdkUtils.crop(getActivity(), imageSqlInfo, true, this);
        } else {
            TuSdkUtils.edit(getActivity(), imageSqlInfo, true, false, this);
        }
    }

    @Override
    public void onAlbumResult(ImageSqlInfo imageSqlInfo) {
        onPhotoResult(imageSqlInfo);
    }

    @Override
    public void onCropResult(File file) {
        onFileResult(file);
    }

    @Override
    public void onEditResult(File file) {
        onFileResult(file);
    }

    private void onFileResult(File file) {
        try {
            String fileUri = file.getAbsolutePath();
            fileUploadService.setFileUploadListener(this);
            fileUploadService.upload(fileUri);
        } catch (Exception e) {
            showError(e);
        }
    }
}