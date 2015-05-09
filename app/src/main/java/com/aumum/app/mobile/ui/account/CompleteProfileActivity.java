package com.aumum.app.mobile.ui.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.user.AreaUsersActivity;
import com.aumum.app.mobile.ui.view.Animation;
import com.aumum.app.mobile.ui.view.dialog.ListViewDialog;
import com.aumum.app.mobile.utils.EditTextUtils;
import com.aumum.app.mobile.utils.ImageLoaderUtils;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.aumum.app.mobile.utils.TuSdkUtils;
import com.greenhalolabs.emailautocompletetextview.EmailAutoCompleteTextView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;

import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class CompleteProfileActivity extends ProgressDialogActivity
    implements Validator.ValidationListener,
               TuSdkUtils.CameraListener,
               TuSdkUtils.AlbumListener,
               TuSdkUtils.CropListener,
        FileUploadService.FileUploadListener {

    @Inject RestService restService;
    @Inject FileUploadService fileUploadService;

    private View saveButton;
    @InjectView(R.id.container) protected View container;
    @InjectView(R.id.image_avatar) protected ImageView avatarImage;
    @InjectView(R.id.et_screen_name) protected EditText screenNameText;

    @InjectView(R.id.et_email)
    @Email(messageResId = R.string.error_incorrect_email_format)
    protected EmailAutoCompleteTextView emailText;

    @InjectView(R.id.et_city) protected EditText cityText;
    @InjectView(R.id.et_area) protected EditText areaText;
    @InjectView(R.id.et_about) protected EditText aboutText;

    private String userId;
    private String screenName;
    private String email;
    private String city;
    private String area;
    private String about;
    private int areaUsersCount;

    private Validator validator;
    private SafeAsyncTask<Boolean> task;
    private final TextWatcher watcher = validationTextWatcher();

    public static final String INTENT_USER_ID = "userId";
    private static final int GET_AREA_USERS_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_complete_profile);
        ButterKnife.inject(this);

        userId = getIntent().getStringExtra(INTENT_USER_ID);
        fileUploadService.init(userId);
        fileUploadService.setFileUploadListener(this);

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraOptions();
            }
        });
        screenNameText.addTextChangedListener(watcher);
        emailText.addTextChangedListener(watcher);
        cityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                container.requestFocus();
                final String cityOptions[] = Constants.Options.CITY_OPTIONS;
                new ListViewDialog(CompleteProfileActivity.this,
                        getString(R.string.label_select_your_city),
                        Arrays.asList(cityOptions),
                        new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        city = cityOptions[i];
                        cityText.setText(cityOptions[i]);
                    }
                }).show();
            }
        });
        cityText.addTextChangedListener(watcher);
        areaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                container.requestFocus();
                if (city == null) {
                    showMsg(R.string.error_city_first);
                    return;
                }
                int cityId = Constants.Options.CITY_ID.get(city);
                startAreaListActivity(cityId);
            }
        });
        areaText.addTextChangedListener(watcher);
        validator = new Validator(this);
        validator.setValidationListener(this);

        Animation.flyIn(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, null);
        menuItem.setActionView(R.layout.menuitem_button_save);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        View view = menuItem.getActionView();
        saveButton = view.findViewById(R.id.b_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
        updateUIWithValidation();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.GET_AREA_LIST_REQ_CODE &&
                resultCode == RESULT_OK) {
            String area = data.getStringExtra(AreaListActivity.INTENT_AREA);
            updateArea(area);
        } else if (requestCode == GET_AREA_USERS_REQ_CODE) {
            setResult(RESULT_OK);
            finish();
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
        final boolean populated = populated(screenNameText) &&
                populated(emailText) &&
                cityText.getText().length() > 0 &&
                areaText.getText().length() > 0;
        if (saveButton != null) {
            saveButton.setEnabled(populated);
        }
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    private void save() {
        if (task != null) {
            return;
        }
        progress.setMessageId(R.string.info_submitting_profile);
        showProgress();
        screenName = screenNameText.getText().toString();
        EditTextUtils.hideSoftInput(screenNameText);
        email = emailText.getText().toString();
        EditTextUtils.hideSoftInput(emailText);
        if (aboutText.getText().length() > 0) {
            about = aboutText.getText().toString();
        }
        EditTextUtils.hideSoftInput(aboutText);
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                if (restService.getScreenNameRegistered(screenName)) {
                    throw new Exception(getString(R.string.error_screen_name_registered));
                }
                if (restService.getEmailRegistered(email)) {
                    throw new Exception(getString(R.string.error_email_registered));
                }
                User user = new User();
                user.setObjectId(userId);
                user.setScreenName(screenName);
                user.setEmail(email);
                user.setCity(city);
                user.setArea(area);
                user.setAbout(about);
                restService.updateUserProfile(user);
                areaUsersCount = restService.getAreaUsersCount(userId, area);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                if(!(e instanceof RetrofitError)) {
                    showError(e);
                }
            }

            @Override
            protected void onSuccess(Boolean success) throws Exception {
                if (areaUsersCount > 0) {
                    startAreaUsersActivity(area);
                } else {
                    setResult(RESULT_OK);
                    finish();
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

    private void updateAvatar(final String fileUrl) {
        if (task != null) {
            return;
        }
        task = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {
                restService.updateUserAvatar(userId, fileUrl);
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
        final Intent intent = new Intent(this, AreaListActivity.class);
        intent.putExtra(AreaListActivity.INTENT_CITY, city);
        startActivityForResult(intent, Constants.RequestCode.GET_AREA_LIST_REQ_CODE);
    }

    private void updateArea(String value) {
        area = value;
        areaText.setText(value);
    }

    @Override
    public void onBackPressed() {
        showMsg(R.string.info_profile_not_completed_yet);
    }

    @Override
    public void onValidationSucceeded() {
        save();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            showMsg(error.getFailedRules().get(0).getMessage(this));
        }
    }

    private void startAreaUsersActivity(String area) {
        final Intent intent = new Intent(this, AreaUsersActivity.class);
        intent.putExtra(AreaUsersActivity.INTENT_AREA, area);
        intent.putExtra(AreaUsersActivity.INTENT_USER_ID, userId);
        intent.putExtra(AreaUsersActivity.INTENT_SHOULD_NOTIFY, true);
        startActivityForResult(intent, GET_AREA_USERS_REQ_CODE);
    }

    @Override
    public void onUploadSuccess(String remoteUrl) {
        updateAvatar(remoteUrl);
    }

    @Override
    public void onUploadFailure(Exception e) {
        showError(e);
    }

    private void showCameraOptions() {
        String options[] = getResources().getStringArray(R.array.label_camera_actions);
        new ListViewDialog(this, null, Arrays.asList(options),
                new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        switch (i) {
                            case 0:
                                TuSdkUtils.camera(CompleteProfileActivity.this, CompleteProfileActivity.this);
                                break;
                            case 1:
                                TuSdkUtils.album(CompleteProfileActivity.this, CompleteProfileActivity.this);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private void onPhotoResult(ImageSqlInfo imageSqlInfo) {
        TuSdkUtils.crop(this, imageSqlInfo, true, this);
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
        try {
            String fileUri = file.getAbsolutePath();
            String avatarUri = ImageLoaderUtils.getFullPath(fileUri);
            Bitmap bitmap = ImageLoaderUtils.loadImage(avatarUri);
            avatarImage.setImageBitmap(bitmap);
            fileUploadService.upload(fileUri);
        } catch (Exception e) {
            showError(e);
        }
    }
}
