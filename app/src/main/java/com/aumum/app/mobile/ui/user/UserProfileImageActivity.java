package com.aumum.app.mobile.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.ReceiveUriScaledBitmapTask;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.view.NetworkImageView;
import com.aumum.app.mobile.ui.view.ProgressDialog;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class UserProfileImageActivity extends Activity
        implements ReceiveUriScaledBitmapTask.ReceiveUriScaledBitmapListener,
                   FileUploadService.OnFileUploadListener {
    public static String INTENT_USER_ID = "intentUserId";
    public static String INTENT_AVATAR_URL = "intentAvatarUrl";

    private String userId;
    private String avatarUrl;
    private Bitmap avatarBitmapCurrent;

    private SafeAsyncTask<Boolean> task;
    private FileUploadService fileUploadService;

    @Inject RestService restService;

    @InjectView(R.id.image_avatar) protected NetworkImageView avatarImage;
    @InjectView(R.id.b_change_avatar) protected Button changeAvatarButton;
    @InjectView(R.id.b_save_avatar) protected Button saveAvatarButton;

    private Uri outputUri;
    private final ProgressDialog progress = ProgressDialog.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_user_profile_image);
        ButterKnife.inject(this);

        final Intent intent = getIntent();
        userId = intent.getStringExtra(INTENT_USER_ID);
        avatarUrl = intent.getStringExtra(INTENT_AVATAR_URL);

        avatarImage.getFromUrl(avatarUrl, R.drawable.ic_avatar);

        changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageSelector();
            }
        });

        saveAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setMessageId(R.string.info_uploading_profile_image);
                showProgress();
                saveUserAvatar();
            }
        });

        fileUploadService = new FileUploadService();
        fileUploadService.setOnFileUploadListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        } else if (requestCode == ImageUtils.GALLERY_INTENT_CALLED && resultCode == Activity.RESULT_OK) {
            Uri originalUri = data.getData();
            if (originalUri != null) {
                progress.setMessageId(R.string.info_loading_image);
                showProgress();
                new ReceiveUriScaledBitmapTask(this, this).execute(originalUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startImageSelector() {
        ImageUtils.getImage(this);
    }

    private void saveUserAvatar() {
        byte[] avatarData = ImageUtils.getBytesBitmap(avatarBitmapCurrent);
        String fileName = userId + ".jpg";
        fileUploadService.upload(fileName, avatarData);
    }

    @Override
    public void onUriScaledBitmapReceived(Uri uri) {
        hideProgress();
        startCropActivity(uri);
    }

    private void startCropActivity(Uri originalUri) {
        outputUri = Uri.fromFile(new File(getCacheDir(), Crop.class.getName()));
        new Crop(originalUri).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            avatarBitmapCurrent = ImageUtils.getBitmap(this, outputUri);
            avatarImage.setImageBitmap(avatarBitmapCurrent);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toaster.showLong(this, R.string.invalid_image);
            Ln.d(Crop.getError(result));
        }
    }

    private synchronized void showProgress() {
        if (!progress.isAdded()) {
            progress.show(getFragmentManager(), null);
        }
    }

    private synchronized void hideProgress() {
        if (progress != null && progress.getActivity() != null) {
            progress.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onUploadSuccess(final String fileUrl) {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.updateUserAvatar(userId, fileUrl);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(UserProfileImageActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onUploadFailure(Exception e) {
        hideProgress();
        Toaster.showLong(UserProfileImageActivity.this, R.string.error_upload_profile_image);
        Ln.d(e);
    }
}
