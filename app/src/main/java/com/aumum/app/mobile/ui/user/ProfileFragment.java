package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.LoaderFragment;
import com.aumum.app.mobile.ui.base.ProgressListener;
import com.aumum.app.mobile.ui.crop.CropImageActivity;
import com.aumum.app.mobile.ui.settings.SettingsActivity;
import com.aumum.app.mobile.ui.view.AvatarImageView;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import javax.inject.Inject;

import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends LoaderFragment<User>
        implements FileUploadService.OnFileUploadListener {

    @Inject RestService restService;
    @Inject UserStore userStore;

    private User currentUser;

    private Bitmap avatarBitmap;
    private byte[] avatarData;
    private SafeAsyncTask<Boolean> task;
    private FileUploadService fileUploadService;
    private ProgressListener progressListener;

    private View mainView;
    private AvatarImageView avatarImage;
    private TextView screenNameText;
    private TextView areaText;
    private TextView aboutText;
    private ViewGroup settingsLayout;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);

        fileUploadService = new FileUploadService();
        fileUploadService.setOnFileUploadListener(this);

        progressListener = (ProgressListener) getActivity();
        progressListener.setMessage(R.string.info_uploading_profile_image);
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
        avatarImage = (AvatarImageView) view.findViewById(R.id.image_avatar);
        screenNameText = (TextView) view.findViewById(R.id.text_screen_name);
        areaText = (TextView) view.findViewById(R.id.text_area);
        aboutText = (TextView) view.findViewById(R.id.text_about);
        settingsLayout = (ViewGroup) view.findViewById(R.id.layout_settings);
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivityForResult(intent, Constants.RequestCode.SETTINGS_REQ_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.GALLERY_INTENT_REQ_CODE &&
            resultCode == Activity.RESULT_OK) {
            Uri originalUri = data.getData();
            if (originalUri != null) {
                startCropImageActivity(originalUri.toString());
            }
        } else if (requestCode == Constants.RequestCode.CROP_PROFILE_IMAGE_REQ_CODE &&
                   resultCode == Activity.RESULT_OK) {
            String avatarImagePath = data.getStringExtra(CropImageActivity.INTENT_BITMAP);
            avatarBitmap = BitmapFactory.decodeFile(avatarImagePath);

            task = new SafeAsyncTask<Boolean>() {
                public Boolean call() throws Exception {
                    avatarData = ImageUtils.getBytesBitmap(avatarBitmap);
                    return true;
                }

                @Override
                protected void onException(final Exception e) throws RuntimeException {
                    if(!(e instanceof RetrofitError)) {
                        final Throwable cause = e.getCause() != null ? e.getCause() : e;
                        if(cause != null) {
                            Toaster.showLong(getActivity(), cause.getMessage());
                        }
                    }
                }

                @Override
                public void onSuccess(final Boolean authSuccess) {
                    progressListener.showProgress();
                    String fileName = currentUser.getObjectId() + ".jpg";
                    fileUploadService.upload(fileName, avatarData);
                }

                @Override
                protected void onFinally() throws RuntimeException {
                    task = null;
                }
            };
            task.execute();
        }
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_load_profile;
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
        return currentUser;
    }

    @Override
    protected void handleLoadResult(final User user) {
        try {
            setData(user);

            avatarImage.getFromUrl(user.getAvatarUrl());
            avatarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startImageSelectionActivity();
                }
            });
            screenNameText.setText(user.getScreenName());
            areaText.setText(Constants.Options.AREA_OPTIONS[user.getArea()]);
            aboutText.setText(user.getAbout());
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    private void startImageSelectionActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, Constants.RequestCode.GALLERY_INTENT_REQ_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.RequestCode.GALLERY_INTENT_REQ_CODE);
        }
    }

    private void startCropImageActivity(String imageUri) {
        final Intent intent = new Intent(getActivity(), CropImageActivity.class);
        intent.putExtra(CropImageActivity.INTENT_TITLE, getString(R.string.title_activity_change_avatar));
        intent.putExtra(CropImageActivity.INTENT_IMAGE_URI, imageUri);
        startActivityForResult(intent, Constants.RequestCode.CROP_PROFILE_IMAGE_REQ_CODE);
    }

    @Override
    public void onUploadSuccess(final String fileUrl) {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.updateUserAvatar(currentUser.getObjectId(), fileUrl);
                currentUser.setAvatarUrl(fileUrl);
                userStore.update(currentUser);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(getActivity(), cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                avatarImage.setImageBitmap(avatarBitmap);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                progressListener.hideProgress();
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onUploadFailure(Exception e) {
        progressListener.hideProgress();
        Toaster.showLong(getActivity(), R.string.error_upload_profile_image);
        Ln.d(e);
    }
}
