package com.aumum.app.mobile.ui.asking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.dao.UserStore;
import com.aumum.app.mobile.core.model.Asking;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.image.CustomGallery;
import com.aumum.app.mobile.ui.image.GalleryAdapter;
import com.aumum.app.mobile.ui.image.ImagePickerActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.ImageUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.github.kevinsawicki.wishlist.Toaster;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class NewAskingActivity extends ProgressDialogActivity
        implements FileUploadService.OnFileUploadListener {

    @Inject UserStore userStore;
    @Inject RestService restService;
    @Inject FileUploadService fileUploadService;

    private int category;
    private SafeAsyncTask<Boolean> task;
    private SafeAsyncTask<Boolean> uploadTask;
    private GalleryAdapter adapter;
    private String imagePathList[];
    private ArrayList<String> imageUrlList;

    public static final String INTENT_CATEGORY = "category";

    private Button submitButton;
    @InjectView(R.id.v_scroll) protected ScrollView scrollView;
    @InjectView(R.id.et_question) protected EditText questionText;
    @InjectView(R.id.text_add_more) protected TextView addMoreText;
    @InjectView(R.id.layout_type_selection) protected ViewGroup typeSelectionLayout;
    @InjectView(R.id.layout_image) protected ViewGroup imageLayout;
    @InjectView(R.id.grid_gallery) protected GridView gridGallery;

    private final TextWatcher watcher = validationTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_new_asking);
        ButterKnife.inject(this);

        imageUrlList = new ArrayList<String>();
        fileUploadService.setOnFileUploadListener(this);

        category = getIntent().getIntExtra(INTENT_CATEGORY, 0);

        progress.setMessageId(R.string.info_submitting_asking);

        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        questionText.addTextChangedListener(watcher);
        addMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTypeSelectionLayout();
            }
        });
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(NewAskingActivity.this, ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.INTENT_ACTION, ImagePickerActivity.ACTION_MULTIPLE_PICK);
                startActivityForResult(intent, Constants.RequestCode.IMAGE_PICKER_IMAGE_REQ_CODE);
            }
        });
        adapter = new GalleryAdapter(this, R.layout.image_collection_listitem_inner, ImageLoaderUtils.getInstance());
        gridGallery.setAdapter(adapter);

        Animation.flyIn(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.label_submit));
        menuItem.setActionView(R.layout.menuitem_button_submit);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        submitButton = (Button) view.findViewById(R.id.b_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        updateUIWithValidation();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.IMAGE_PICKER_IMAGE_REQ_CODE) {
            toggleTypeSelectionLayout();
            if (resultCode == Activity.RESULT_OK) {
                imagePathList = data.getStringArrayExtra(ImagePickerActivity.INTENT_ALL_PATH);
                if (imagePathList != null) {
                    ArrayList<CustomGallery> list = new ArrayList<CustomGallery>();
                    for (String path : imagePathList) {
                        CustomGallery item = new CustomGallery();
                        item.type = CustomGallery.FILE;
                        item.imageUri = path;
                        list.add(item);
                    }
                    adapter.addAll(list);
                }
            }
        }
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(questionText);
        submitButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void submit() {
        EditTextUtils.hideSoftInput(questionText);

        imageUrlList.clear();
        if (imagePathList != null) {
            for (String path : imagePathList) {
                uploadImage(path);
            }
        } else {
            submitNewAsking();
        }
    }

    private void uploadImage(final String imagePath) {
        if (uploadTask != null) {
            return;
        }
        showProgress();
        uploadTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                byte avatarData[] = ImageUtils.decodeBitmap(imagePath);
                fileUploadService.upload(imagePath, avatarData);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(NewAskingActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                uploadTask = null;
            }
        };
        uploadTask.execute();
    }

    private void submitNewAsking() {
        if (task != null) {
            return;
        }
        showProgress();
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                User user = userStore.getCurrentUser();
                Asking asking = new Asking(
                        user.getObjectId(),
                        category,
                        questionText.getEditableText().toString(),
                        imageUrlList);
                Asking response = restService.newAsking(asking);
                restService.addUserAsking(user.getObjectId(), response.getObjectId());
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showShort(NewAskingActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean success) {
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toaster.showShort(NewAskingActivity.this, R.string.error_submit_new_asking);
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                task = null;
            }
        };
        task.execute();
    }

    private void toggleTypeSelectionLayout() {
        if (typeSelectionLayout.getVisibility() == View.GONE) {
            Animation.flyIn(typeSelectionLayout);
        } else {
            Animation.flyOut(typeSelectionLayout);
        }
    }

    @Override
    public void onUploadSuccess(String fileUrl) {
        imageUrlList.add(fileUrl);
        if (imageUrlList.size() == imagePathList.length) {
            submitNewAsking();
        }
    }

    @Override
    public void onUploadFailure(Exception e) {
        hideProgress();
        Toaster.showShort(NewAskingActivity.this, R.string.error_submit_new_asking);
    }
}
