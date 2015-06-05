package com.aumum.app.mobile.ui.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aumum.app.mobile.Injector;
import com.aumum.app.mobile.R;
import com.aumum.app.mobile.core.Constants;
import com.aumum.app.mobile.core.model.Moment;
import com.aumum.app.mobile.core.model.User;
import com.aumum.app.mobile.core.service.FileUploadService;
import com.aumum.app.mobile.core.service.RestService;
import com.aumum.app.mobile.ui.area.AreaListActivity;
import com.aumum.app.mobile.ui.base.ProgressDialogActivity;
import com.aumum.app.mobile.ui.helper.TextWatcherAdapter;
import com.aumum.app.mobile.ui.view.AvatarImageView;
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
import java.util.ArrayList;
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

    @InjectView(R.id.container) protected View container;
    @InjectView(R.id.image_avatar) protected AvatarImageView avatarImage;
    @InjectView(R.id.et_screen_name) protected EditText screenNameText;

    @InjectView(R.id.et_email)
    @Email(messageResId = R.string.error_incorrect_email_format)
    protected EmailAutoCompleteTextView emailText;

    @InjectView(R.id.et_city) protected EditText cityText;
    @InjectView(R.id.et_area) protected EditText areaText;
    @InjectView(R.id.et_about) protected EditText aboutText;
    @InjectView(R.id.b_save) protected Button saveButton;

    private String userId;
    private String country;
    private String avatarUrl;
    private String screenName;
    private String email;
    private String city;
    private String area;
    private String about;

    private Validator validator;
    private SafeAsyncTask<Boolean> task;
    private final TextWatcher watcher = validationTextWatcher();

    public static final String INTENT_USER_ID = "userId";
    public static final String INTENT_COUNTRY = "country";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
        setContentView(R.layout.activity_complete_profile);
        ButterKnife.inject(this);

        userId = getIntent().getStringExtra(INTENT_USER_ID);
        country = getIntent().getStringExtra(INTENT_COUNTRY);
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
                final ArrayList<String> cities = new ArrayList(
                        Constants.Map.CITY.get(country).keySet());
                new ListViewDialog(CompleteProfileActivity.this,
                        getString(R.string.label_select_your_city),
                        cities,
                        new ListViewDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int i) {
                        city = cities.get(i);
                        cityText.setText(city);
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
                int cityId = Constants.Map.CITY.get(country).get(city);
                startAreaListActivity(cityId);
            }
        });
        areaText.addTextChangedListener(watcher);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
        validator = new Validator(this);
        validator.setValidationListener(this);

        updateUIWithValidation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RequestCode.GET_AREA_LIST_REQ_CODE &&
                resultCode == RESULT_OK) {
            area = data.getStringExtra(AreaListActivity.INTENT_AREA);
            areaText.setText(area);
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
        final boolean populated = avatarUrl != null &&
                populated(screenNameText) &&
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
                user.setAvatarUrl(avatarUrl);
                user.setScreenName(screenName);
                user.setEmail(email);
                user.setCountry(country);
                user.setCity(city);
                user.setArea(area);
                user.setAbout(about);
                restService.updateUserProfile(user);
                Moment moment = new Moment(userId,
                        getString(R.string.label_first_moment), avatarUrl);
                moment = restService.newMoment(moment);
                restService.addUserFirstMoment(userId, moment.getObjectId());
                user.addMoment(moment.getObjectId());
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

    private void startAreaListActivity(int city) {
        final Intent intent = new Intent(this, AreaListActivity.class);
        intent.putExtra(AreaListActivity.INTENT_CITY, city);
        startActivityForResult(intent, Constants.RequestCode.GET_AREA_LIST_REQ_CODE);
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

    @Override
    public void onUploadSuccess(String remoteUrl) {
        avatarUrl = remoteUrl;
        avatarImage.setBorderColor(getResources().getColor(R.color.am_white));
        updateUIWithValidation();
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
