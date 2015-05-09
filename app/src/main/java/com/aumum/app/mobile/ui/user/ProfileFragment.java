package com.aumum.app.mobile.ui.user;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.ScrollView;
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
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.settings.SettingsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.ui.view.dialog.ConfirmDialog;
import com.aumum.app.mobile.ui.view.dialog.EditTextDialog;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.ui.view.dialog.TextViewDialog;
import com.aumum.app.mobile.ui.view.pulltorefresh.XGridView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.TuSdkUtils;
import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import commons.validator.routines.EmailValidator;
import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends LoaderFragment<User>
        implements TuSdkUtils.CameraListener,
                   TuSdkUtils.AlbumListener,
                   TuSdkUtils.CropListener,
                   TuSdkUtils.EditListener,
        FileUploadService.FileUploadListener {

    @Inject RestService restService;
    @Inject UserStore userStore;
    @Inject MomentStore momentStore;
    @Inject FileUploadService fileUploadService;

    private User currentUser;
    private List<String> album;
    private SafeAsyncTask<Boolean> task;
    private ImageView imageToEdit;

    private View mainView;
    private AlbumAdapter albumAdapter;
    private ImageView coverImage;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView emailText;
    private TextView cityText;
    private TextView areaText;
    private TextView aboutText;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.inject(this);

        album = new ArrayList<>();
        fileUploadService.setFileUploadListener(this);
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
                    startSettingsActivity();
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

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        
        mainView = view.findViewById(R.id.main_view);
        XGridView userView = (XGridView) view.findViewById(R.id.user_view);
        userView.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
        StaggeredGridView staggeredView = userView.getRefreshableView();
        View header = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_profile_header, null, false);
        staggeredView.addHeaderView(header);
        initHeaderView(header);

        albumAdapter = new AlbumAdapter(getActivity());
        userView.setAdapter(albumAdapter);
    }

    private void initHeaderView(View view) {
        coverImage = (ImageView) view.findViewById(R.id.image_cover);
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        View screenNameLayout = view.findViewById(R.id.layout_screen_name);
        screenNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(getActivity(), R.layout.dialog_edit_text, R.string.hint_screen_name,
                        new EditTextDialog.OnConfirmListener() {
                            @Override
                            public void call(Object value) throws Exception {
                                String screenName = (String) value;
                                if (restService.getScreenNameRegistered((String) value)) {
                                    throw new Exception(getString(R.string.error_screen_name_registered));
                                }
                                restService.updateUserScreenName(currentUser.getObjectId(), screenName);
                                currentUser.setScreenName(screenName);
                                userStore.save(currentUser);
                            }

                            @Override
                            public void onException(String errorMessage) {
                                showMsg(errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String screenName = (String) value;
                                screenNameText.setText(screenName);
                            }
                        }).show();
            }
        });
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        View emailLayout = view.findViewById(R.id.layout_email);
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(getActivity(), R.layout.dialog_edit_text, R.string.hint_email,
                        new EditTextDialog.OnConfirmListener() {
                            @Override
                            public void call(Object value) throws Exception {
                                String email = (String) value;
                                if (!EmailValidator.getInstance().isValid(email)) {
                                    throw new Exception(getString(R.string.error_incorrect_email_format));
                                }
                                if (restService.getEmailRegistered(email)) {
                                    throw new Exception(getString(R.string.error_email_registered));
                                }
                                restService.updateUserEmail(currentUser.getObjectId(), email);
                                currentUser.setEmail(email);
                                userStore.save(currentUser);
                            }

                            @Override
                            public void onException(String errorMessage) {
                                showMsg(errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String email = (String) value;
                                emailText.setText(email);
                            }
                        }).show();
            }
        });
        emailText = (TextView) view.findViewById(R.id.text_email);
        View cityLayout = view.findViewById(R.id.layout_city);
        cityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cityOptions[] = Constants.Options.CITY_OPTIONS;
                new ListViewDialog(getActivity(),
                        getString(R.string.label_select_your_city),
                        Arrays.asList(cityOptions),
                        new ListViewDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int i) {
                                final String city = cityOptions[i];
                                final String text = getString(R.string.label_confirm_city, city);
                                new TextViewDialog(getActivity(), text, new ConfirmDialog.OnConfirmListener() {
                                    @Override
                                    public void call(Object value) throws Exception {
                                        restService.updateUserCity(currentUser.getObjectId(), city);
                                        currentUser.setCity(city);
                                        userStore.save(currentUser);
                                    }

                                    @Override
                                    public void onException(String errorMessage) {
                                        showMsg(errorMessage);
                                    }

                                    @Override
                                    public void onSuccess(Object value) {
                                        cityText.setText(city);
                                    }
                                }).show();
                            }
                        }).show();
            }
        });
        cityText = (TextView) view.findViewById(R.id.text_city);
        View areaLayout = view.findViewById(R.id.layout_area);
        areaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cityId = Constants.Options.CITY_ID.get(currentUser.getCity());
                startAreaListActivity(cityId);
            }
        });
        areaText = (TextView) view.findViewById(R.id.text_area);
        View aboutLayout = view.findViewById(R.id.layout_about);
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(getActivity(), R.layout.dialog_edit_text_multiline, R.string.hint_about,
                        new ConfirmDialog.OnConfirmListener() {
                            @Override
                            public void call(Object value) throws Exception {
                                String about = (String) value;
                                restService.updateUserAbout(currentUser.getObjectId(), about);
                                currentUser.setAbout(about);
                                userStore.save(currentUser);
                            }

                            @Override
                            public void onException(String errorMessage) {
                                showMsg(errorMessage);
                            }

                            @Override
                            public void onSuccess(Object value) {
                                String about = (String) value;
                                aboutText.setText(about);
                            }
                        }).show();
            }
        });
        aboutText = (TextView) view.findViewById(R.id.text_about);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.GET_AREA_LIST_REQ_CODE &&
                resultCode == Activity.RESULT_OK) {
            String area = data.getStringExtra(AreaListActivity.INTENT_AREA);
            updateArea(area);
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
        List<Moment> momentList = momentStore.loadMore(currentUser.getMoments(), null);
        for (Moment moment: momentList) {
            album.add(fileUploadService.getThumbnail(moment.getImageUrl()));
        }
        return currentUser;
    }

    @Override
    protected void handleLoadResult(final User user) {
        if (user != null) {
            setData(user);

            albumAdapter.addAll(album);
            if (user.getCoverUrl() != null) {
                ImageLoaderUtils.displayImage(user.getCoverUrl(),
                        coverImage, R.drawable.photo_placeholder);
            } else {
                coverImage.setImageResource(
                        Constants.Options.CITY_COVER.get(user.getCity()));
            }
            coverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageToEdit = coverImage;
                    TuSdkUtils.album(getActivity(), ProfileFragment.this);
                }
            });
            avatarImage.getFromUrl(user.getAvatarUrl());
            avatarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageToEdit = avatarImage;
                    showCameraOptions();
                }
            });
            screenNameText.setText(user.getScreenName());
            emailText.setText(user.getEmail());
            cityText.setText(user.getCity());
            areaText.setText(user.getArea());
            aboutText.setText(user.getAbout());
        }
    }

    private void updateAvatar(final String avatarUrl) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.updateUserAvatar(currentUser.getObjectId(), avatarUrl);
                currentUser.setAvatarUrl(avatarUrl);
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

    private void startAreaListActivity(int city) {
        final Intent intent = new Intent(getActivity(), AreaListActivity.class);
        intent.putExtra(AreaListActivity.INTENT_CITY, city);
        startActivityForResult(intent, Constants.RequestCode.GET_AREA_LIST_REQ_CODE);
    }

    private void updateArea(final String area) {
        final String text = getString(R.string.label_confirm_area, area);
        new TextViewDialog(getActivity(), text, new ConfirmDialog.OnConfirmListener() {
            @Override
            public void call(Object value) throws Exception {
                restService.updateUserArea(currentUser.getObjectId(), area);
                currentUser.setArea(area);
                userStore.save(currentUser);
            }

            @Override
            public void onException(String errorMessage) {
                showMsg(errorMessage);
            }

            @Override
            public void onSuccess(Object value) {
                areaText.setText(area);
            }
        }).show();
    }

    private void startSettingsActivity() {
        final Intent intent = new Intent(getActivity(), SettingsActivity.class);
        getActivity().startActivityForResult(intent, Constants.RequestCode.SETTINGS_REQ_CODE);
    }

    @Override
    public void onUploadSuccess(String remoteUrl) {
        if (imageToEdit == avatarImage) {
            updateAvatar(remoteUrl);
        } else if (imageToEdit == coverImage) {
            updateCover(remoteUrl);
        }
    }

    @Override
    public void onUploadFailure(Exception e) {
        showError(e);
    }

    private void showCameraOptions() {
        String options[] = getResources().getStringArray(R.array.label_camera_actions);
        new ListViewDialog(getActivity(), null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                TuSdkUtils.camera(getActivity(), ProfileFragment.this);
                                break;
                            case 1:
                                TuSdkUtils.album(getActivity(), ProfileFragment.this);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void onPhotoResult(ImageSqlInfo imageSqlInfo) {
        if (imageToEdit == avatarImage) {
            TuSdkUtils.crop(getActivity(), imageSqlInfo, true, this);
        } else if (imageToEdit == coverImage) {
            Bitmap bitmap = BitmapHelper.getBitmap(imageSqlInfo);
            if (bitmap.getHeight() > bitmap.getWidth()) {
                TuSdkUtils.crop(getActivity(), imageSqlInfo, true, this);
            } else {
                TuSdkUtils.edit(getActivity(), bitmap, true, false, this);
            }
        }
    }

    @Override
    public void onCameraResult(ImageSqlInfo imageSqlInfo) {
        onPhotoResult(imageSqlInfo);
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
            String imageUri = ImageLoaderUtils.getFullPath(fileUri);
            Bitmap bitmap = ImageLoaderUtils.loadImage(imageUri);
            imageToEdit.setImageBitmap(bitmap);
            fileUploadService.upload(fileUri);
        } catch (Exception e) {
            showError(e);
        }
    }
}