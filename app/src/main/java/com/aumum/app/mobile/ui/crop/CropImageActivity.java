package com.aumum.app.mobile.ui.crop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.Ln;
import com.github.kevinsawicki.wishlist.Toaster;

import java.io.File;

public class CropImageActivity extends ProgressDialogActivity {

    private CropImageView cropImage;

    public static final String INTENT_TITLE = "title";
    public static final String INTENT_IMAGE_URI = "imageUri";
    public static final String INTENT_BITMAP = "bitmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        setTitle(getIntent().getStringExtra(INTENT_TITLE));

        String imageUri = getIntent().getStringExtra(INTENT_IMAGE_URI);
        cropImage = (CropImageView) findViewById(R.id.image_crop);
        ImageLoaderUtils.displayImage(imageUri, cropImage,
                new ImageLoaderUtils.ImageLoadingListener() {
            @Override
            public void onLoadingStarted() {
                progress.setMessageId(R.string.info_loading_image);
                showProgress();
            }

            @Override
            public void onLoadingFailed() {
                hideProgress();
                Toaster.showLong(CropImageActivity.this, R.string.error_load_image);
            }

            @Override
            public void onLoadingComplete() {
                hideProgress();
            }

            @Override
            public void onLoadingCancelled() {
                hideProgress();
            }
        });
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
                progress.setMessageId(R.string.info_uploading_profile_image);
                showProgress();
                save();
            }
        });
        return true;
    }

    private void save() {
        try {
            File file = ImageUtils.getFileFromBitmap(this, cropImage.clip());
            final Intent intent = new Intent();
            intent.putExtra(INTENT_BITMAP, file.getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            Ln.e(e);
        }
    }
}
