package com.aumum.app.mobile.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.crop.CropImageView;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.io.File;

import javax.inject.Inject;

import retrofit.RetrofitError;

public class UpdateAvatarActivity extends ProgressDialogActivity
        implements FileUploadService.OnFileUploadListener {

    @Inject FileUploadService fileUploadService;

    private String imageUri;
    private String imagePath;
    private CropImageView cropImage;
    private SafeAsyncTask<Boolean> task;

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_IMAGE_URI = "imageUri";
    public static final String INTENT_IMAGE_URL = "imageUrl";
    public static final String INTENT_IMAGE_PATH = "imagePath";

    private static final int AVATAR_MAX_WIDTH = 180;
    private static final int AVATAR_MAX_HEIGHT = 180;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_crop_image);

        setTitle(getIntent().getStringExtra(INTENT_TITLE));

        imageUri = getIntent().getStringExtra(INTENT_IMAGE_URI);
        cropImage = (CropImageView) findViewById(R.id.image_crop);
        ImageLoaderUtils.displayImage(imageUri, cropImage);

        fileUploadService.setOnFileUploadListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_save));
        menuItem.setActionView(R.layout.menuitem_button_save);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        Button saveButton = (Button) view.findViewById(R.id.b_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        return true;
    }

    private void save() {
        if (task != null) {
            return;
        }
        progress.setMessageId(R.string.info_uploading_avatar);
        showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                byte bytes[] = ImageUtils.getBytesBitmap(cropImage.clip(AVATAR_MAX_WIDTH, AVATAR_MAX_HEIGHT));
                imagePath = ImageUtils.createFile(UpdateAvatarActivity.this, bytes).getAbsolutePath();
                fileUploadService.upload(imageUri, bytes);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(UpdateAvatarActivity.this, cause.getMessage());
                    }
                }
                hideProgress();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                task = null;
            }
        };
        task.execute();
    }

    @Override
    public void onUploadSuccess(String fileUrl) {
        hideProgress();
        final Intent intent = new Intent();
        intent.putExtra(INTENT_IMAGE_URL, fileUrl);
        intent.putExtra(INTENT_IMAGE_PATH, imagePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onUploadFailure(Exception e) {
        hideProgress();
        Toaster.showShort(this, R.string.error_upload_avatar);
        Ln.e(e);
    }
}
