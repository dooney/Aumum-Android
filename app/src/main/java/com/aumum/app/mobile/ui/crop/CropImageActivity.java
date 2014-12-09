package com.aumum.app.mobile.ui.crop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import retrofit.RetrofitError;

public class CropImageActivity extends ProgressDialogActivity
        implements FileUploadService.OnFileUploadListener {

    private String imageUri;
    private CropImageView cropImage;
    private SafeAsyncTask<Boolean> task;
    private FileUploadService fileUploadService;

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_IMAGE_URI = "imageUri";
    public static final String INTENT_IMAGE_URL = "imageUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        setTitle(getIntent().getStringExtra(INTENT_TITLE));

        imageUri = getIntent().getStringExtra(INTENT_IMAGE_URI);
        cropImage = (CropImageView) findViewById(R.id.image_crop);
        ImageLoaderUtils.displayImage(imageUri, cropImage);

        fileUploadService = new FileUploadService();
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
                progress.setMessageId(R.string.info_uploading_image);
                showProgress();
                save();
            }
        });
        return true;
    }

    private void save() {
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                byte bytes[] = ImageUtils.getBytesBitmap(cropImage.clip());
                String fileName = Math.abs(imageUri.hashCode()) + ".jpg";
                fileUploadService.upload(fileName, bytes);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(CropImageActivity.this, cause.getMessage());
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
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onUploadFailure(Exception e) {
        hideProgress();
        Toaster.showShort(this, R.string.error_upload_image);
        Ln.e(e);
    }
}
